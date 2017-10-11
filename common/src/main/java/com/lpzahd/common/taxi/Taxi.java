package com.lpzahd.common.taxi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 *
 * Taxi 对象a拉取数据的时候对象b发送
 * 对象b 需要注册
 */
final class Taxi {

    private ConcurrentMap<Object, Transmitter> buses = new ConcurrentHashMap<>();

    public void regist(Object tag, Transmitter receiver) {
        throwIfNull(tag, "tag");
        buses.put(tag, receiver);
    }

    public void registIfAbsent(Object tag, Transmitter receiver) {
        throwIfNull(tag, "tag");
        buses.putIfAbsent(tag, receiver);
    }

    public void unregist(Object tag) {
        buses.remove(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> Transmitter<T> pull(Object tag) {
        return buses.get(tag);
    }

    public void clear() {
        buses.clear();
    }

    private void throwIfNull(Object obj, String msg) {
        if(obj == null)
            throw new NullPointerException(msg + " is null");
    }

}
