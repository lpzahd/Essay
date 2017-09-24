package com.lpzahd;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public abstract class Effort implements Value {

    private final int initialValue = 1;

    int value = initialValue;

    @Override
    public int value() {
        return value;
    }

    @Override
    public int devalue(int value) {
        return this.value -= value;
    }

    @Override
    public int revaluate(int value) {
        return this.value += value;
    }

}
