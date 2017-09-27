package com.lpzahd.waiter;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public interface LifecleCallBack<T> {

    void created(T t);

    void started(T t);

    void resumed(T t);

    void paused(T t);

    void stopped(T t);

    void destroyed(T t);

    void saveInstanceState(T t);
}
