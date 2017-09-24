package com.lpzahd.base;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class NoInstance {

    protected NoInstance() {
        throw new AssertionError("No instances for you!");
    }
}
