package com.lpzahd.atool;

import org.junit.Test;

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

    static CallBack callBack = new CallBack() {
        @Override
        public void fun() {
            System.out.println("cb : fun !");
        }
    };

    @Test
    public void testCallBack() {

        new Thread(new CloseCallBack()).start();

        Fun fun = new Fun();
//        fun.setCallBack(callBack);

        new Thread(fun)
                .start();

        while (true);
    }

    public static class CloseCallBack implements Runnable {

        public CloseCallBack() {
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                callBack = null;
                System.out.println("callback 变成了 : " + callBack);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Fun implements Runnable {

//        CallBack callBack;
//
//        public void setCallBack(CallBack callBack) {
//            this.callBack = callBack;
//        }

        @Override
        public void run() {
            int index = 0;
            while (true) {
                try {
                    Thread.sleep(2000);
                    System.out.println("count : " + index);
                    callBack.fun();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static interface CallBack {
        void fun();
    }
}