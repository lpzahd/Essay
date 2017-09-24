package com.lpzahd.animview;

import com.google.common.math.IntMath;

import org.junit.Test;

import java.math.RoundingMode;

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
    public void math() throws Exception {
        double ans = IntMath.sqrt(2, RoundingMode.FLOOR);
        System.out.println("ans : " + ans);
        ans =  Math.sqrt(2);
        System.out.println("ans : " + ans);
    }
}