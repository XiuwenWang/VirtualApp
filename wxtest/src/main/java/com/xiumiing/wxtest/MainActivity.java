package com.xiumiing.wxtest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.lody.virtual.client.core.VirtualCore;

import static com.xiumiing.wxtest.VirtualHelper.WE_CHAT_PACKAGE_NAME;

public class MainActivity extends android.app.Activity {

    public static final String TAG = "MainActivity";

    int userId = 100;
    int cloneCount = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        //检查是否安装 宿主微信
        if (!VirtualHelper.getInstance().isInstalledWeChat()) {
            Log.d(TAG, "没有安装微信: ");
            return;
        }
//        VirtualCore.get().installPackage(WE_CHAT_PACKAGE_NAME,0);
        for (int i = 0; i < cloneCount; i++) {
            userId = i + 1;
            VirtualHelper.getInstance().installedAsUser(userId);
            VirtualHelper.getInstance().createShortcut(userId);
//                    VirtualHelper.getInstance().removeShortcut(userId);
        }
        VirtualHelper.getInstance().launchApp(userId);
    }

    public void onClick1(View view) {
        startActivity(new Intent(this,ShoutCutActivity.class));
    }
}
