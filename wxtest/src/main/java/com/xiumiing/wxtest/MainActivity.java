package com.xiumiing.wxtest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lody.virtual.GmsSupport;
import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        VirtualCore.get().installPackage("com.tencent.mm", 0);
//        Intent intent = VirtualCore.get().getLaunchIntent("com.tencent.mm", 0);
//        VActivityManager.get().startActivity(intent, 0);


        PackageManager packageManager = getPackageManager();
        if (packageManager.isInstantApp("com.tencent.mm")) {
            if (VirtualCore.get().isAppInstalledAsUser(0,"com.tencent.mm")) {

            }
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo("com.tencent.mm", 0);
                AppInfo appInfo = convertPackageInfoToAppData(this,packageInfo,true);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }




//        int pid = android.os.Process.myPid();
//        Log.i(TAG, "进程id:" + pid);
//        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
//            if (appProcess.pid == pid) {
//                Log.i(TAG, "进程名:" + appProcess.processName);
//            }
//        }
//        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(info);
//        Log.i(TAG, "系统剩余内存:" + (info.availMem >> 20) + "MB");
//        Log.i(TAG, "系统总内存:" + (info.totalMem >> 20) + "MB");
//        Log.i(TAG, "系统是否处于低内存运行：" + info.lowMemory);
//        Log.i(TAG, "当系统剩余内存低于" + (info.threshold >> 20) + "MB时就看成低内存运行");
//        Runtime rt = Runtime.getRuntime();
//        Log.d(TAG, "Available heap " + (rt.freeMemory() >> 20) + "MB");
//        Log.d(TAG, "MAX heap " + (rt.maxMemory() >> 20) + "MB");
//        Log.d(TAG, "totle heap " + (rt.totalMemory() >> 20) + "MB");


//            VirtualCore.get().startup(this);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        InstallResult installResult = VirtualCore.get().installPackage("com.tencent.mm", 0);
//        VirtualCore.get().createShortcut()
    }

    private AppInfo convertPackageInfoToAppData(Context context, PackageInfo pkg, boolean fastOpen) {
        Log.e("tag ", "convertPackageInfoToAppData");
        PackageManager pm = context.getPackageManager();
        String hostPkg = VirtualCore.get().getHostPkg();
        Log.e("tag hostPkg", hostPkg);
        Log.e("tag hostPkg", pkg.packageName);
        // ignore the host package
        if (hostPkg.equals(pkg.packageName)) {
            return null;
        }
        // ignore the System package
        if (isSystemApplication(pkg)) {
            return null;
        }
        ApplicationInfo ai = pkg.applicationInfo;
        String path = ai.publicSourceDir != null ? ai.publicSourceDir : ai.sourceDir;
        if (path == null) {
            return null;
        }
        AppInfo info = new AppInfo();
        info.packageName = pkg.packageName;
        info.fastOpen = fastOpen;
        info.path = path;
        info.icon = ai.loadIcon(pm);
        info.name = ai.loadLabel(pm);
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(pkg.packageName, 0);
        if (installedAppInfo != null) {
            info.cloneCount = installedAppInfo.getInstalledUsers().length;
        }
        return info;
    }

    private static boolean isSystemApplication(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                && !GmsSupport.isGmsFamilyPackage(packageInfo.packageName);
    }

    public void addApp(AppInfo info) {
        class AddResult {
            private PackageAppData appData;
            private int userId;
            private boolean justEnableHidden;
        }
        AddResult addResult = new AddResult();
        VUiKit.defer()
                .when(() -> {
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
                        InstallResult res = addVirtualApp(info);
                        if (!res.isSuccess) {
                            throw new IllegalStateException();
                        }
                    }
                })
                .then((res) -> {
                    addResult.appData = PackageAppDataStorage.get().acquire(info.packageName);
                })
                .done(res -> {
                    boolean multipleVersion = addResult.justEnableHidden && addResult.userId != 0;
                    if (!multipleVersion) {
                        PackageAppData data = addResult.appData;
                        data.isLoading = true;
                        mView.addAppToLauncher(data);
                        handleOptApp(data, info.packageName, true);
                    } else {
                        MultiplePackageAppData data = new MultiplePackageAppData(addResult.appData, addResult.userId);
                        data.isLoading = true;
                        mView.addAppToLauncher(data);
                        handleOptApp(data, info.packageName, false);
                    }
                });
    }
    public InstallResult addVirtualApp(AppInfo info) {
        int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT;
        if (info.fastOpen) {
            flags |= InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
        }
        return VirtualCore.get().installPackage(info.path, flags);
    }

    private void handleOptApp(AppData data, String packageName, boolean needOpt) {
        VUiKit.defer().when(() -> {
            long time = System.currentTimeMillis();
            if (needOpt) {
                try {
                    VirtualCore.get().preOpt(packageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            time = System.currentTimeMillis() - time;
            if (time < 1500L) {
                try {
                    Thread.sleep(1500L - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).done((res) -> {
            if (data instanceof PackageAppData) {
                ((PackageAppData) data).isLoading = false;
                ((PackageAppData) data).isFirstOpen = true;
            } else if (data instanceof MultiplePackageAppData) {
                ((MultiplePackageAppData) data).isLoading = false;
                ((MultiplePackageAppData) data).isFirstOpen = true;
            }
            mView.refreshLauncherItem(data);
        });
    }



    public void onClick(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }
}
