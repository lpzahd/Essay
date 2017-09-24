package com.lpzahd.gallery;

import android.os.SystemClock;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void t() {
        int a = 0;
        a = 1 & 2;
        a = 2 & 2;
        a = 3 & 2;
        a = 4 & 2;
        a = 5 & 2;
    }

    private static boolean stopRequested;

    @Test
    public void test() throws InterruptedException {
        Thread backgroundThread = new Thread(new Runnable() {
            public void run() {
                int i = 0;
                while (!stopRequested) {
                    i++;
                    //这段System.out语句会导致线程结束，原因？
//                    System.out.println(i);
                }
            }
        });
        backgroundThread.start();
        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;

        for (; ; ) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(backgroundThread.isAlive());
        }
    }

    @Test
    public void gc() throws InterruptedException {
        WeakReference<A> a = new WeakReference<A>(new A());
        SoftReference<B> b = new SoftReference<B>(new B());
        a.get().fun();
        b.get().fun();

//        System.gc();
//        Thread.sleep(5000L);

        int aNum = 0;
        int bNum = 0;

        while (true) {
            Thread.sleep(4000L);
            System.gc();
            Thread.sleep(500L);
            A weakA = a.get();
            if (weakA != null) {
                weakA.fun();
            } else {
                System.out.println("A 没了");
                a = new WeakReference<A>(new A());
                System.out.println("A 儿子" + (++aNum) + "号出来了");
            }

            if (b == null) {
                b = new SoftReference<B>(new B());
                System.out.println("B 儿子" + (++bNum) + "号出来了");
            }
            B weakB = b.get();
            if (weakB != null) {
                weakB.fun();
                b = null;
                System.out.println("B 儿子" + (++bNum) + "号被弄死了");
            } else {
                System.out.println("B 没了");
                b = new SoftReference<B>(new B());
                System.out.println("B 儿子" + (++bNum) + "号出来了");
            }
        }
    }

    public class A {
        @Override
        protected void finalize() throws Throwable {
            System.out.println("A 要死了");

            super.finalize();
        }

        public void fun() {
            System.out.println("A 干活了");
        }
    }

    public class B {
        @Override
        protected void finalize() throws Throwable {
            System.out.println("B 要死了");

            super.finalize();
        }

        public void fun() {
            System.out.println("B 干活了");
        }
    }

    private static final HashMap<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        // 图片格式
        FILE_TYPE_MAP.put("jpg", "FFD8FF");
        FILE_TYPE_MAP.put("png", "89504E47");
        FILE_TYPE_MAP.put("gif", "47494638");
        FILE_TYPE_MAP.put("tif", "49 49 2A 00");
        FILE_TYPE_MAP.put("bmp", "424D");

        FILE_TYPE_MAP.put("zip", "504B0304");
        FILE_TYPE_MAP.put("rar", "52617221");

        FILE_TYPE_MAP.put("dwg", "41433130");

        FILE_TYPE_MAP.put("psd", "38425053");
        FILE_TYPE_MAP.put("rtf", "7B5C727466");
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        FILE_TYPE_MAP.put("html", "3C68746D6C");

        // EML格式是微软公司在Outlook中所使用的一种遵循RFC822及其后续扩展的文件格式，并成为各类电子邮件软件的通用格式。
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");
        FILE_TYPE_MAP.put("pst", "2142444E");
        FILE_TYPE_MAP.put("office", "D0CF11E0");
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");

        FILE_TYPE_MAP.put("wpd", "FF 57 50 43");
        FILE_TYPE_MAP.put("eps or ps", "25 21 50 53 2D 41 64 6F 62 65");
        FILE_TYPE_MAP.put("pdf", "25 50 44 46 2D 31 2E");
        FILE_TYPE_MAP.put("qdf", "AC 9E BD 8F");
        FILE_TYPE_MAP.put("pwl", "AC 9E BD 8F");

        FILE_TYPE_MAP.put("wav", "57 41 56 45");
        FILE_TYPE_MAP.put("avi", "41 56 49 20");
        FILE_TYPE_MAP.put("ram", "2E 72 61 FD");
        FILE_TYPE_MAP.put("rm", "2E 52 4D 46");
        FILE_TYPE_MAP.put("mpg", "00 00 01 BA");

        FILE_TYPE_MAP.put("mpg", "00 00 01 B3");
        FILE_TYPE_MAP.put("mov", "6D 6F 6F 76");
        FILE_TYPE_MAP.put("asf", "30 26 B2 75 8E 66 CF 11");
        FILE_TYPE_MAP.put("mid", "4D 54 68 64");
    }


}