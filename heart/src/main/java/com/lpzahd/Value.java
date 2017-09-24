package com.lpzahd;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public interface Value {

    /**
     * 价值几何
     */
    int value();

    /**
     * 贬值
     */
    int devalue(int value);

    /**
     * 升值
     */
    int revaluate(int value);

    /**
     * 最大价值
     */
    int maxValue();

    /**
     * 最低价值
     */
    int minValue();
}
