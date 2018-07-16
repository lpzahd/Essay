package com.lpzahd.atool.keeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.lpzahd.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
     *
     * @param bitmap   源图片
     * @param fileName 文件的全路径名(不包括后缀)
     * @param format   图片格式
     */
    public void save(Bitmap bitmap, String fileName, Bitmap.CompressFormat format) {
        if (format == null) {
            format = Bitmap.CompressFormat.PNG;
        }

        String suffix = getSuffix(format);
        File file = new File(fileName + "." + suffix);
        if (file.exists()) {
            boolean deleteOk = file.delete();
            if (!deleteOk) return;
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

    public static Drawable zoomDrawable(Context context, BitmapDrawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = drawable.getBitmap();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(context.getResources(), bmp);
    }

    /**
     * 纯色bitmap
     */
    public static Bitmap colorBitmap(@ColorInt int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
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

    // Drawable转换成Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Bitmap转换成byte[]
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // Drawable转换成byte[]
    public static byte[] Drawable2Bytes(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return bitmap2Bytes(bitmap);
    }

    // 将Bitmap转换成InputStream
    public static InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }


    // Drawable转换成InputStream
    public static InputStream drawable2InputStream(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return bitmap2InputStream(bitmap);
    }


}
