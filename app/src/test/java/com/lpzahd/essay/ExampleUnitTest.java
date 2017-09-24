package com.lpzahd.essay;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.lpzahd.Maths;
import com.lpzahd.atool.constant.Constance;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void join() {
        String a = "a";
        final int size = 100;

        String str = "";
        long nano = System.nanoTime();
        for (int i = 0; i < size; i++) {
            str += a;
        }
        System.out.println(" +  : " + (System.nanoTime() - nano));

        long nano1 = System.nanoTime();
        StringBuilder builder = new StringBuilder(size);
        builder.append(str);
        for (int i = 0; i < size; i++) {
            builder.append(a);
        }
        System.out.println(" builder  : " + (System.nanoTime() - nano1));

    }

    @Test
    public void fileSeparator() {

        System.out.print(filePath("a", "b", "c","d","e"));
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

    @Test
    public void tobinary() {
        int aVal = Integer.valueOf("1", 2);
        int bVal = Integer.valueOf("11", 2);
        System.out.println(Integer.toBinaryString(aVal + bVal));
    }

}