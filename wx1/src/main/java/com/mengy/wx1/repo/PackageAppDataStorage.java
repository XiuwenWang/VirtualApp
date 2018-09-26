package com.mengy.wx1.repo;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;
import com.mengy.wx1.Callback;
import com.mengy.wx1.VApp;
import com.mengy.wx1.models.PackageAppData;
import com.mengy.wx1.ui.VUiKit;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Lody
 *         <p>
 *         Cache the loaded PackageAppData.
 */
public class PackageAppDataStorage {

    private static final PackageAppDataStorage STORAGE = new PackageAppDataStorage();
    private final Map<String, PackageAppData> packageDataMap = new HashMap<>();

    public static PackageAppDataStorage get() {
        return STORAGE;
    }

    public PackageAppData acquire(String packageName) {
        PackageAppData data;
        synchronized (packageDataMap) {
            data = packageDataMap.get(packageName);
            if (data == null) {
                data = loadAppData(packageName);
            }
        }
        return data;
    }

    public void acquire(String packageName, Callback<PackageAppData> callback) {
        VUiKit.defer()
                .when(() -> acquire(packageName))
                .done(callback::callback);
    }

    private PackageAppData loadAppData(String packageName) {
        InstalledAppInfo setting = VirtualCore.get().getInstalledAppInfo(packageName, 0);
        if (setting != null) {
            PackageAppData data = new PackageAppData(VApp.getApp(), setting);
            synchronized (packageDataMap) {
                packageDataMap.put(packageName, data);
            }
            return data;
        }
        return null;
    }

}
