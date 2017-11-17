package com.lpzahd.atool.ui;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;


/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ) toast
 */
public class T {

    private static Application app;
    private static Handler handler;

    public static void init(Application app) {
        T.app = app;
    }

    public static void t(CharSequence msg) {
        Toast.makeText(app, msg, Toast.LENGTH_SHORT).show();
    }

    public static void t(@StringRes int resId) {
        Toast.makeText(app, app.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void t(String format, Object... args) {
        t(String.format(format, args));
    }

    public static void post(final CharSequence msg) {
        handler().post(new Runnable() {
            @Override
            public void run() {
                t(msg);
            }
        });
    }

    private static Handler handler() {
        return handler != null ? handler : (handler = new Handler(Looper.getMainLooper()));
    }
}
