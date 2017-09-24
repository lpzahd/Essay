package com.lpzahd.atool.ui;

import android.util.Log;

import com.lpzahd.Config;
import com.lpzahd.base.NoInstance;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class L extends NoInstance {

    public static void e(String msg) {
        if(Config.DEBUG)
            Log.e(Config.LOG, msg);
    }

    public static void e(String format, Object... argus) {
        if(Config.DEBUG)
            Log.e(Config.LOG, String.format(format, argus));
    }

    public static void e(Exception exc) {
        if(Config.DEBUG)
            Log.e(Config.LOG, exc.getMessage());
    }
}
