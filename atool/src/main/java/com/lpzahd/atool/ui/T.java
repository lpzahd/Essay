package com.lpzahd.atool.ui;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ) toast
 */
public class T {

    private static Application app;

    public static void init(Application app) {
        T.app = app;
    }

    public static void t(CharSequence msg) {
        Toast.makeText(app, msg, Toast.LENGTH_SHORT).show();
    }

    public static void t(@StringRes int resId) {
        Toast.makeText(app, app.getString(resId), Toast.LENGTH_SHORT).show();
    }
}
