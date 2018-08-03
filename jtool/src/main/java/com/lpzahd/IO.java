package com.lpzahd;

import com.lpzahd.base.NoInstance;

import java.io.Closeable;
import java.io.Flushable;

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
