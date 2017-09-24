package com.lpzahd.atool.keeper;

import android.app.Application;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Keeper {

    public static void init(Application application) {
        Files.getF().init(application);
    }

    public static Files getF() {
        return Files.getF();
    }

    public static Bitmaps getBt() {
        return Bitmaps.getBitmaps();
    }

}
