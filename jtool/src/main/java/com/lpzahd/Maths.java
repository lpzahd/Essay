package com.lpzahd;

import com.lpzahd.base.NoInstance;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Maths extends NoInstance {

    /**
     * 是否奇数
     */
    public static boolean isOdd(int num) {
        return (num & 0x01) == 1;
    }

}
