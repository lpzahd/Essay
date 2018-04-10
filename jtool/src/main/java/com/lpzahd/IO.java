package com.lpzahd;

import com.lpzahd.base.NoInstance;

import java.io.Closeable;
import java.io.Flushable;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class IO extends NoInstance {

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception ignore) {
        }
    }

    public static void flushQuietly(Flushable flushable) {
        if (flushable == null) return;
        try {
            flushable.flush();
        } catch (Exception ignore) {
        }
    }

}
