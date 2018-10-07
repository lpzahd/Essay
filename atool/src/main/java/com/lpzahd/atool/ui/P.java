package com.lpzahd.atool.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.lpzahd.base.NoInstance;

/**
 * 作者 : Administrator
 * 时间 : 2018/10/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class P extends NoInstance {

    private static Application app;

    public static void init(Application app) {
        P.app = app;
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    public static void set(String key, @Nullable String value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String get(String key) {
        return get(key, "");
    }

    public static String get(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

}
