package com.mengy.wx1;

import android.content.Intent;
import android.os.Bundle;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.mengy.wx1.models.AppData;
import com.mengy.wx1.models.AppInfoLite;
import com.mengy.wx1.models.MultiplePackageAppData;
import com.mengy.wx1.models.PackageAppData;
import com.mengy.wx1.repo.AppRepository;
import com.mengy.wx1.repo.PackageAppDataStorage;
import com.mengy.wx1.ui.VActivity;
import com.mengy.wx1.ui.VUiKit;

public class MainActivity extends VActivity {
    private AppRepository mRepo;
    private AppData mTempAppData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InstallResult installResult = VirtualCore.get().installPackage("com.tencent.mm", 0);

//        mRepo = new AppRepository(this);
//
//        ComponentDelegate componentDelegate = VirtualCore.get().getComponentDelegate();
//        mRepo.getVirtualApps().done(new AndroidDoneCallback<List<AppData>>() {
//            @Override
//            public void onDone(List<AppData> result) {
//
//            }
//
//            @Override
//            public AndroidExecutionScope getExecutionScope() {
//                return null;
//            }
//        }).fail(new AndroidFailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//
//            }
//
//            @Override
//            public AndroidExecutionScope getExecutionScope() {
//                return null;
//            }
//        });
//
////        AppInfoLite info = new AppInfoLite();
////        mRepo.addVirtualApp(info);
////        mRepo.getStorageApps()
//        VirtualCore.OnEmitShortcutListener listener = new VirtualCore.OnEmitShortcutListener() {
//            @Override
//            public Bitmap getIcon(Bitmap originIcon) {
//                return originIcon;
//            }
//
//            @Override
//            public String getName(String originName) {
//                return originName + "(VA)";
//            }
//        };
//
////        MultiplePackageAppData appData = (MultiplePackageAppData) data;
////        VirtualCore.get().createShortcut(appData.userId, appData.appInfo.packageName, listener);


    }

    /**
     * 创建快捷方式
     *
     * @param name
     * @param icon
     * @param intent
     */
    public void installShortCut(String name, int icon, Intent intent) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        shortcut.putExtra("duplicate", false); // 不允许重复创建

        // 快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        intent.setAction("android.intent.action.MAIN");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
        intent.addCategory("android.intent.category.LAUNCHER");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        this.sendBroadcast(shortcut);
    }


    public void addApp(AppInfoLite info) {
        class AddResult {
            private PackageAppData appData;
            private int userId;
            private boolean justEnableHidden;
        }
        AddResult addResult = new AddResult();
        VUiKit.defer().when(() -> {
            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(info.packageName, 0);
            addResult.justEnableHidden = installedAppInfo != null;
            if (addResult.justEnableHidden) {
                int[] userIds = installedAppInfo.getInstalledUsers();
                int nextUserId = userIds.length;
                /*
                  Input : userIds = {0, 1, 3}
                  Output: nextUserId = 2
                 */
                for (int i = 0; i < userIds.length; i++) {
                    if (userIds[i] != i) {
                        nextUserId = i;
                        break;
                    }
                }
                addResult.userId = nextUserId;

                if (VUserManager.get().getUserInfo(nextUserId) == null) {
                    // user not exist, create it automatically.
                    String nextUserName = "Space " + (nextUserId + 1);
                    VUserInfo newUserInfo = VUserManager.get().createUser(nextUserName, VUserInfo.FLAG_ADMIN);
                    if (newUserInfo == null) {
                        throw new IllegalStateException();
                    }
                }
                boolean success = VirtualCore.get().installPackageAsUser(nextUserId, info.packageName);
                if (!success) {
                    throw new IllegalStateException();
                }
            } else {
                InstallResult res = mRepo.addVirtualApp(info);
                if (!res.isSuccess) {
                    throw new IllegalStateException();
                }
            }
        }).then((res) -> {
            addResult.appData = PackageAppDataStorage.get().acquire(info.packageName);
        }).done(res -> {
            boolean multipleVersion = addResult.justEnableHidden && addResult.userId != 0;
            if (!multipleVersion) {
                PackageAppData data = addResult.appData;
                data.isLoading = true;
//                mView.addAppToLauncher(data);
//                handleOptApp(data, info.packageName, true);
            } else {
                MultiplePackageAppData data = new MultiplePackageAppData(addResult.appData, addResult.userId);
                data.isLoading = true;
//                mView.addAppToLauncher(data);
//                handleOptApp(data, info.packageName, false);
            }
        });
    }
}
