package com.lpzahd.waiter.waiter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class WaiterTeam<E extends Waiter> extends ArrayList<E> {

    private static final int DEFAULT_CAPACITY = 8;

    private Waiter waiter;

    public WaiterTeam(Waiter waiter) {
        this(DEFAULT_CAPACITY);
        this.waiter = waiter;
    }

    public WaiterTeam(int initialCapacity) {
        super(initialCapacity);
    }

    public WaiterTeam<E> addWaiter(E e) {
        e.setBoss(waiter);
        add(e);
        return this;
    }

    public WaiterTeam<E> removeWaiter(E e) {
        e.setBoss(null);
        remove(e);
        return this;
    }

    public void sort() {
        Collections.sort(this);
    }

}
