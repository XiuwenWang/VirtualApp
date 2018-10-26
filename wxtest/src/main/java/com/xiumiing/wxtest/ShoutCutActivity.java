package com.xiumiing.wxtest;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ShoutCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_cut);

    }

    //添加快捷方式
    private void createShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager scm = (ShortcutManager) getSystemService(SHORTCUT_SERVICE);
            Intent launcherIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);//设置网络页面intent
            ShortcutInfo si = new ShortcutInfo.Builder(this, "dataroam")
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setShortLabel("网络设置")
                    .setIntent(launcherIntent)
                    .build();
            assert scm != null;
            scm.requestPinShortcut(si, null);
        } else {
            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT")
                    .putExtra("duplicate", false)
                    .putExtra(Intent.EXTRA_SHORTCUT_NAME, "网络设置")
                    .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher))
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
            sendBroadcast(addShortcutIntent);
        }

    }

    public void deleteShortCut() {
        Intent addShortcutIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT")
                .putExtra("duplicate", false)
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, "网络设置")
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher))
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
        sendBroadcast(addShortcutIntent);
    }


    public void onClick(View view) {
        if (((Button) view).getText().equals("创建")) {
            createShortcut();
        } else if (((Button) view).getText().equals("删除")) {
            deleteShortCut();
        }

    }
}
