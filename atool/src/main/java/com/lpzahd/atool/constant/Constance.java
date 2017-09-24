package com.lpzahd.atool.constant;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lpzahd.base.NoInstance;

import java.io.File;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Constance extends NoInstance {

    public static int SCREEN_WIDTH, SCREEN_HEIGHT;

    public static String AppVersionName;
    public static int AppVersionCode;
    public static String AppName;
    public static String AppPackageName;

    public static void initApp(Context context) {
        initDeviceInfo(context);
        getAppVersionInfo(context);
    }

    private static void initDeviceInfo(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        SCREEN_WIDTH = Math.min(metrics.widthPixels, metrics.heightPixels);
        SCREEN_HEIGHT = Math.max(metrics.widthPixels, metrics.heightPixels);

    }

    /**
     * 获取当前版本的版本信息
     */
    private static void getAppVersionInfo(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            // ---get the package info---
            packageManager = context.getPackageManager();
            AppPackageName = context.getPackageName();
            PackageInfo pi = packageManager.getPackageInfo(AppPackageName, 0);
            AppVersionName = pi.versionName;
            AppVersionCode = pi.versionCode;
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }

        if (applicationInfo != null) {
            AppName = (String) packageManager.getApplicationLabel(applicationInfo);
        }
    }

    /**
     * @return 换行符
     */
    public static String lineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * @return 路径分隔符
     */
    public static String fileSeparator() {
        return File.separator;
    }
}
