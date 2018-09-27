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

public class ShoutCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_cut);
    }

    //添加快捷方式
    private void addShortcut() {
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
            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");//"com.android.launcher.action.INSTALL_SHORTCUT"
            // 不允许重复创建
            addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
            // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
            // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
            // 屏幕上没有空间时会提示
            // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

            // 名字
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "网络设置");
            // 图标
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));

            // 设置关联程序
            Intent launcherIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);//设置网络页面intent
            // 设置关联程序
//        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
//        launcherIntent.setClass(MainActivity.this, MainActivity.class);
//        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

            // 发送广播
            sendBroadcast(addShortcutIntent);
        }

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

//        intent.setAction("android.intent.action.MAIN");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
//        intent.addCategory("android.intent.category.LAUNCHER");// 桌面图标和应用绑定，卸载应用后系统会同时自动删除图标
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        this.sendBroadcast(shortcut);
    }

    public void onClick(View view) {
//        addShortcut();
        installShortCut("测试快捷方式",R.mipmap.ic_launcher,new Intent(Settings.ACTION_APPLICATION_SETTINGS));
    }
}
