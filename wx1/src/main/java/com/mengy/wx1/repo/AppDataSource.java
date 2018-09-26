package com.mengy.wx1.repo;

import android.content.Context;

import com.lody.virtual.remote.InstallResult;
import com.mengy.wx1.models.AppData;
import com.mengy.wx1.models.AppInfo;
import com.mengy.wx1.models.AppInfoLite;

import org.jdeferred.Promise;

import java.io.File;
import java.util.List;


/**
 * @author Lody
 * @version 1.0
 */
public interface AppDataSource {

    /**
     * @return All the Applications we Virtual.
     */
    Promise<List<AppData>, Throwable, Void> getVirtualApps();

    /**
     * @param context Context
     * @return All the Applications we Installed.
     */
    Promise<List<AppInfo>, Throwable, Void> getInstalledApps(Context context);

    Promise<List<AppInfo>, Throwable, Void> getStorageApps(Context context, File rootDir);

    InstallResult addVirtualApp(AppInfoLite info);

    boolean removeVirtualApp(String packageName, int userId);
}
