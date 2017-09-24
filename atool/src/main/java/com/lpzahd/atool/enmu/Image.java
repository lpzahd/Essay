package com.lpzahd.atool.enmu;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Image {

    // 远程图片
    public static final int SOURCE_NET = 1;

    // 本地图片
    public static final int SOURCE_FILE = 2;

    // res目录下的图片
    public static final int SOURCE_RES = 3;

    // asset目录下的图片
    public static final int SOURCE_ASSET = 4;

    @IntDef({SOURCE_NET, SOURCE_FILE, SOURCE_RES, SOURCE_ASSET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SOURCE {

    }
}
