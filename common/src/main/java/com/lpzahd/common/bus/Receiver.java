package com.lpzahd.common.bus;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public interface Receiver<T> {

    void receive(Flowable<T> flowable);

}
