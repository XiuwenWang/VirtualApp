package com.xiumiing.wxtest;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends android.app.Activity {

    public static final String TAG = "MainActivity";

    int userId = 1;
    int cloneCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查是否安装 宿主微信
        if (!VirtualHelper.getInstance().isInstalledWeChat()) {
            Log.d(TAG, "没有安装微信: ");
            return;
        }
        //是否安装 虚拟 微信 Log.d(TAG, "没有安装微信: ");
        for (int i = 0; i < cloneCount; i++) {
            userId = i + 1;
            VirtualHelper.getInstance().installedAsUser(userId);
            VirtualHelper.getInstance().createShortcut(userId);
            VirtualHelper.getInstance().launchApp(userId);
        }
    }
}
