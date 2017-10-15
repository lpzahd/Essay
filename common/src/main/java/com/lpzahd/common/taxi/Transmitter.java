package com.lpzahd.common.taxi;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public interface Transmitter<T> {

    Flowable<T> transmit();

}
