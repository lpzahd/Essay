package com.lpzahd.atool.keeper;

import android.graphics.Bitmap;

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
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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
}
