package com.lpzahd.waiter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    public void asyn() {
        List<Integer> list = new ArrayList<>();
        for (int i  = 0 ; i < 10; i++) {
            list.add(i);
        }


        for (int i = list.size() - 1; i >= 0; i--) {
            System.out.println(list.get(i));
            list.remove(Integer.valueOf(3));
        }

    }
}