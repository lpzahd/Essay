package com.lpzahd.atool.keeper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.lpzahd.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Bitmaps {

    private static Bitmaps bt;

    private Bitmaps() {
    }

    static Bitmaps getBitmaps() {
        return bt == null ? bt = new Bitmaps() : bt;
    }

    public void save(Bitmap bitmap, String fileName) {
        save(bitmap, fileName, Bitmap.CompressFormat.PNG);
    }

    /**
     * 保存图片
     * @param bitmap    源图片
     * @param fileName  文件的全路径名(不包括后缀)
     * @param format    图片格式
     */
    public void save(Bitmap bitmap, String fileName, Bitmap.CompressFormat format) {
        if(format == null) {
            format = Bitmap.CompressFormat.PNG;
        }

        String suffix = getSuffix(format);
        File file = new File(fileName + "." + suffix);
        if(file.exists()) {
            boolean deleteOk = file.delete();
            if(!deleteOk) return ;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            bitmap.compress(format, 100, fos);

            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IO.closeQuietly(fos);
        }

    }

    /**
     * 获取图片后缀
     */
    private String getSuffix(Bitmap.CompressFormat format) {
        String suffix = "png";
        switch (format) {
            case JPEG:
                suffix = "jpg";
                break;
            case PNG:
                suffix = "png";
                break;
            case WEBP:
                suffix = "webp";
                break;
        }
        return suffix;
    }

    /**
     * 纯色bitmap
     */
    public static Bitmap colorBitmap(@ColorInt int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    /**
     * base64编码 (png格式 )
     */
    public static String toBase64(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            baos.flush();
            byte[] byteArray = baos.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException ignored) {
        } finally {
            IO.closeQuietly(baos);
        }
        return null;

    }
}
