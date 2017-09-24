package com.lpzahd.waiter.waiter;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class Gradation {

    public static final int GRADATION_HIGH = 10000;
    public static final int GRADATION_NORMAL = 100;
    public static final int GRADATION_LOW = 1;

    private int gradation = GRADATION_NORMAL;

    public Gradation() {}

    public Gradation(int gradation) {
        this.gradation = gradation;
    }

    public int getGradation() {
        return gradation;
    }

    public void setGradation(int gradation) {
        this.gradation = gradation;
    }

    public static int compare(Waiter w1, Waiter w2) {
        return w2.getGradation() - w1.getGradation();
    }
}
