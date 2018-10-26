package com.xiumiing.wxtest;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;

/**
 * ----------BigGod be here!----------/
 * ***┏┓******┏┓*********
 * *┏━┛┻━━━━━━┛┻━━┓*******
 * *┃             ┃*******
 * *┃     ━━━     ┃*******
 * *┃             ┃*******
 * *┃  ━┳┛   ┗┳━  ┃*******
 * *┃             ┃*******
 * *┃     ━┻━     ┃*******
 * *┃             ┃*******
 * *┗━━━┓     ┏━━━┛*******
 * *****┃     ┃神兽保佑*****
 * *****┃     ┃代码无BUG！***
 * *****┃     ┗━━━━━━━━┓*****
 * *****┃              ┣┓****
 * *****┃              ┏┛****
 * *****┗━┓┓┏━━━━┳┓┏━━━┛*****
 * *******┃┫┫****┃┫┫********
 * *******┗┻┛****┗┻┛*********
 * ━━━━━━神兽出没━━━━━━
 * 版权所有：个人
 * 作者：Created by a.wen.
 * 创建时间：2018/9/27
 * Email：13872829574@163.com
 * 内容描述：
 * 修改人：a.wen
 * 修改时间：${DATA}
 * 修改备注：
 * 修订历史：1.0
 */
public class VirtualHelper {
    public static final String TAG = "VirtualHelper";
    public static final String WE_CHAT_PACKAGE_NAME = "com.tencent.mm";
    private static VirtualHelper instance;
    private PackageManager pm;

    public VirtualHelper() {
        pm = App.getApp().getPackageManager();
    }

    public static VirtualHelper getInstance() {
        if (instance == null) {
            synchronized (VirtualHelper.class) {
                if (instance == null) {
                    instance = new VirtualHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 安装并且 启动虚拟微信
     *
     * @param userId 用户Id
     */
    public void installLaunchApp(int userId) {
        //是否安装 虚拟 微信 Log.d(TAG, "没有安装微信: ");
        if (VirtualHelper.getInstance().isInstalledAsUser(userId)) {
            VirtualHelper.getInstance().launchApp(userId);
        } else {
            if (!VirtualHelper.getInstance().installedAsUser(userId)) {
                Log.d(TAG, "安装虚拟微信: " + userId + "失败");
                return;
            }
            VirtualHelper.getInstance().launchApp(userId);
        }
    }

    /**
     * 多用户安装
     *
     * @param userIds
     */
    public void installedAsUsers(int[] userIds) {
        for (int i = 0; i < userIds.length; i++) {
            installedAsUser(userIds[i]);
        }
    }

    /**
     * 创建快捷方式
     *
     * @param userId
     */
    public void createShortcut(int userId) {
        VirtualCore.get().createShortcut(userId, WE_CHAT_PACKAGE_NAME, new VirtualCore.OnEmitShortcutListener() {
            @Override
            public Bitmap getIcon(Bitmap originIcon) {
                return originIcon;
            }

            @Override
            public String getName(String originName) {
                return "微信" + userId;
            }
        });
    }

    /**
     * 创建快捷方式
     *
     * @param userId
     */
    public void removeShortcut(int userId) {
        VirtualCore.get().removeShortcut(userId, WE_CHAT_PACKAGE_NAME, null,new VirtualCore.OnEmitShortcutListener() {
            @Override
            public Bitmap getIcon(Bitmap originIcon) {
                return originIcon;
            }

            @Override
            public String getName(String originName) {
                return "微信" + userId;
            }
        });
    }

    /**
     * 用户 userId 是否安装
     *
     * @param appInfo
     */
    public void launchApp(AppInfo appInfo) {
        try {
            LoadingActivity.launch(App.getApp(), appInfo.packageName, appInfo.userId);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户 userId 是否安装
     *
     * @param userId
     */
    public void launchApp(int userId) {
        //获取微信包信息
        AppInfo appInfo = getWeChatInfo();
        appInfo.userId = userId;
        launchApp(appInfo);
    }

    /**
     * 用户 userId 安装
     *
     * @param userId 0
     * @return
     */
    public boolean uninstallPackageAsUser(int userId) {
       return VirtualCore.get().uninstallPackageAsUser(WE_CHAT_PACKAGE_NAME,userId);
    }
    /**
     * 用户 userId 安装
     *
     * @param userId 0
     * @return
     */
    public boolean installedAsUser(int userId) {
        Log.d(TAG, "userId = " + userId);
        //获取微信包信息
        AppInfo appInfo = getWeChatInfo();
        if (appInfo == null) {
            Log.e(TAG, "appInfo" + "获取微信安装信息 == null");
            return false;
        }
        //获取安装包
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(appInfo.packageName, 0);
        if (installedAppInfo == null) {
            int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT;
            if (appInfo.fastOpen) {
                flags |= InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
            }
            InstallResult res = VirtualCore.get().installPackage(appInfo.path, flags);
            Log.d(TAG, "InstallResult" + res.toString());
            if (!res.isSuccess) {
                Log.e(TAG, "安装虚拟微信包 == false");
                return false;
            }
        }
        if (installedAppInfo == null) {
            InstalledAppInfo info = VirtualCore.get().getInstalledAppInfo(appInfo.packageName, 0);
            Log.d(TAG, "installedAppInfo" + info.toString());
        }
        //创建 用户
        VUserInfo newUserInfo = VUserManager.get().createUser("微信" + userId, VUserInfo.FLAG_ADMIN);
        if (newUserInfo == null) {
            Log.e(TAG, "createUser" + "创建 用户  == null");
            return false;
        }
        Log.d(TAG, "newUserInfo" + newUserInfo.toString());
        //安装包 作为用户
        boolean success = VirtualCore.get().installPackageAsUser(userId, appInfo.packageName);
        if (!success) {
            Log.e(TAG, "installPackageAsUser" + "安装包 作为用户  == false");
            return false;
        }
        return true;
    }


    /**
     * 获取微信安装信息
     *
     * @return AppInfo
     */
    public AppInfo getWeChatInfo() {
        AppInfo info = new AppInfo();
        try {
            PackageInfo pkg = pm.getPackageInfo(WE_CHAT_PACKAGE_NAME, 0);
            ApplicationInfo ai = pkg.applicationInfo;
            String path = ai.publicSourceDir != null ? ai.publicSourceDir : ai.sourceDir;
            info.packageName = pkg.packageName;
            info.fastOpen = true;
            info.path = path;
            info.icon = ai.loadIcon(pm);
            info.name = ai.loadLabel(pm);
            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(pkg.packageName, 0);
            if (installedAppInfo != null) {
                info.cloneCount = installedAppInfo.getInstalledUsers().length;
            }
            Log.d(TAG, "拿到微信安装包信息：" + info.toString());
            if (info.path == null) return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "异常" + e.getMessage());
        }
        return info;
    }

    /**
     * 用户 userId 是否安装
     *
     * @param userId 0
     * @return
     */
    public boolean isInstalledAsUser(int userId) {
        return VirtualCore.get().isAppInstalledAsUser(userId, WE_CHAT_PACKAGE_NAME);
    }

    /**
     * 是否安装微信
     *
     * @return
     */
    public boolean isInstalledWeChat() {
        try {
            if (isInstalledApp(WE_CHAT_PACKAGE_NAME)) {
                Log.d(TAG, "安装微信: ");
                return true;
            } else {
                Log.e(TAG, "没有安装微信: ");
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isInstalledApp(String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
