package com.lpzahd.common.bus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 *
 * Bus A对象注册并等待对象b推送数据
 */
public final class Bus {

    private ConcurrentMap<Object, Receiver> buses = new ConcurrentHashMap<>();

    public <T> void regist(Object tag, Receiver<T> receiver) {
        throwIfNull(tag, "tag");
        buses.put(tag, receiver);
    }

    public <T> void registIfAbsent(Object tag, Receiver<T> receiver) {
        throwIfNull(tag, "tag");
        buses.putIfAbsent(tag, receiver);
    }

    public void unregist(Object tag) {
        buses.remove(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> void post(Object tag, T argu) {
        Receiver<T> bus = buses.get(tag);
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
