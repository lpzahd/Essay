package com.lpzahd.waiter.waiter;

import android.support.annotation.NonNull;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class Waiter<T extends Waiter> implements Comparable<T> {

    // boss
    private Waiter boss;

    // 级别
    private Gradation gradation;

    protected WaiterTeam<T> waiterTeam;

    public WaiterTeam<T> workTeam() {
        return waiterTeam;
    }

    public WaiterTeam bossTeam() {
        if (boss == null) return null;
        return boss.workTeam();
    }

    public Waiter() {
        this(null);
    }

    public Waiter(int gradation) {
        this(null, gradation);
    }

    public Waiter(Waiter boss) {
        this.gradation = new Gradation();
        this.boss = boss;
        waiterTeam = new WaiterTeam<>(this);
    }

    public Waiter(Waiter boss, int gradation) {
        this.gradation = new Gradation(gradation);
        this.boss = boss;
        waiterTeam = new WaiterTeam<>(this);
    }

    public Waiter<T> addWaiter(T worker) {
        waiterTeam.addWaiter(worker);
        return this;
    }

    public void removeWaiter(T worker) {
        waiterTeam.removeWaiter(worker);
    }

    /**
     * if U want to be divorced from the team
     */
    public void removeSelf() {
        if (boss != null)
            boss.waiterTeam.remove(this);
    }

    public void clear() {
        waiterTeam.clear();
    }

    /**
     * if U want the team is orderly
     */
    public Waiter<T> sort() {
        waiterTeam.sort();
        return this;
    }

    public void setBoss(T worker) {
        this.boss = worker;
    }

    public Waiter getBoss() {
        return boss;
    }

    public void setGradation(int gradation) {
        this.gradation.setGradation(gradation);
        if (boss != null) {
            boss.sort();
        }
    }

    public int getGradation() {
        return gradation.getGradation();
    }


    @Override
    public int compareTo(@NonNull T o) {
        return Gradation.compare(this, o);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " gradation : " + gradation;
    }
}
