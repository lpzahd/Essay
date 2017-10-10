package com.lpzahd.common.bus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
final class Bus {

    private ConcurrentMap<Object, Receiver> buses = new ConcurrentHashMap<>();

    public void regist(Object tag, Receiver receiver) {
        throwIfNull(tag, "tag");
        buses.put(tag, receiver);
    }

    public void registIfAbsent(Object tag, Receiver receiver) {
        throwIfNull(tag, "tag");
        buses.putIfAbsent(tag, receiver);
    }

    public void unregist(Object tag) {
        buses.remove(tag);
    }

    public <T> void post(Object tag, T argu) {
        Receiver bus = buses.get(tag);
        if(bus != null)
            bus.receive(Flowable.just(argu));
    }

    public void clear() {
        buses.clear();
    }

    private void throwIfNull(Object obj, String msg) {
        if(obj == null)
            throw new NullPointerException(msg + " is null");
    }
}
