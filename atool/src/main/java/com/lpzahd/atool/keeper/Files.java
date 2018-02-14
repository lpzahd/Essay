package com.lpzahd.atool.keeper;

import android.app.Application;
import android.os.Environment;

import com.lpzahd.Objects;
import com.lpzahd.atool.constant.Constance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;

import android.support.annotation.StringDef;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 * 对，没错，就是这个files
 * 就是他，霸占了存取文件的路口。
 */
public class Files {

    private static Files f;

    private Files() {
    }

    static Files getF() {
        return f == null ? f = new Files() : f;
    }

    private String rootDir;

    void init(Application app) {
        rootDir = getRootDir(app);
    }

    public String getScopePath(@FileScope String scope) {
        String path = filePath(rootDir, scope);
        mkDirsIfNotExist(path);
        return path;
    }

    public File getScopeFile(@FileScope String scope) {
        return new File(getScopePath(scope));
    }

    public String getFilePath(@FileScope String scope, String name) {
        return filePath(getScopePath(scope), name);
    }

    public File getFile(@FileScope String scope, String name) {
        return new File(getFilePath(scope, name));
    }

    private String getRootDir(Application app) {
        Objects.requireNonNull(app, "the app is null");

        String dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            dir = app.getFilesDir().getAbsolutePath();
        }
        return dir;
    }

    private boolean mkDirsIfNotExist(String dirPath) {
        File dir = new File(dirPath);
        return !dir.exists() && dir.mkdirs();
    }

    /**
     * 地址路径的拼接
     */
    private static String filePath(CharSequence... chars) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i != 0) {
                builder.append(Constance.fileSeparator());
            }
            builder.append(chars[i]);
        }
        return builder.toString();
    }

    @StringDef({Scope.CACHE, Scope.FRESCO, Scope.DATABASE,
            Scope.VIDEO_RAW, Scope.VIDEO_THUMB, Scope.AUDIO_RAW, Scope.AUDIO_THUMB,
            Scope.PHOTO_RAW, Scope.PHOTO_THUMB, Scope.PHOTO_PRIVATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileScope {
    }

    /**
     * 文件作用域
     */
    public static final class Scope {

        private static final String ROOT = "Lpzahd";

        private static final String FILE = ROOT + "/" + "file";
        private static final String MEDIA = ROOT + "/" + "media";

        private static final String VIDEO = MEDIA + "/" + "video";
        private static final String AUDIO = MEDIA + "/" + "audio";
        private static final String PHOTO = MEDIA + "/" + "photo";

        public static final String VIDEO_RAW = VIDEO + "/" + "raw";
        public static final String VIDEO_THUMB = VIDEO + "/" + "thumb";

        public static final String AUDIO_RAW = AUDIO + "/" + "raw";
        public static final String AUDIO_THUMB = AUDIO + "/" + "thumb";

        public static final String PHOTO_RAW = PHOTO + "/" + "raw";
        public static final String PHOTO_THUMB = PHOTO + "/" + "thumb";

        /**
         * 私人图库,图片格式一律隐藏，不希望被系统扫描到
         */
        public static final String PHOTO_PRIVATE = PHOTO + "/" + "private";


        public static final String FILE_RAW = FILE + "/" + "raw";

        public static final String DATABASE = ROOT + "/" + "database";

        public static final String CACHE = ROOT + "/" + "cache";
        public static final String FRESCO = CACHE + "/" + "fresco";

    }

    public static boolean streamToFile(InputStream is, String path) {
        FileOutputStream fos = null;
        int len;
        byte[] buf = new byte[2048];
        try {
            fos = new FileOutputStream(path);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException ignored) {
            }
        }
    }
}
