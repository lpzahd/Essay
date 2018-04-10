package com.lpzahd.derive.charset;

import com.lpzahd.IO;
import com.lpzahd.base.NoInstance;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class CharDet extends NoInstance {

    /**
     * 获取字符串编码格式
     */
    public static String getCharsetFromFile(String file) {
        return getCharsetFromFile(new File(file));
    }

    /**
     * 获取字符串编码格式
     */
    public static String getCharsetFromFile(File file) {
        byte[] content = new byte[1024];
        InputStream in = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            in.read(content);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            IO.closeQuietly(fis);
            IO.closeQuietly(in);
        }

        return getCharset(content);
    }

    /**
     * 获取字符串编码格式
     * @param content  内容，建议1024个
     * @return 字符集
     */
    public static String getCharset(byte[] content) {
        UniversalDetector detector = Holder.detector;
        detector.handleData(content, 0, content.length);
        detector.dataEnd();
        String charset = detector.getDetectedCharset();
        detector.reset();
        return charset;
    }

    private static class Holder {
        static UniversalDetector detector = new UniversalDetector(null);
    }
}
