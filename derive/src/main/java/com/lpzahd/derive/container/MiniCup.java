package com.lpzahd.derive.container;

import com.lpzahd.NoHeart;
import com.lpzahd.Objects;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ) 迷你小杯
 */
public class MiniCup<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    private static final float DEFAULT_LOAD_FACTOR = 0.25f;

    private Map<E, Object> cup;

    private int autoTag;

    private CupHeart heart;

    public MiniCup() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MiniCup(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public MiniCup(int initialCapacity, float loadFactor) {
        cup = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    {
        heart = new CupHeart();
    }

    public Object put(E e) {
        return cup.put(e, autoTag++);
    }

    public Object putAgain(E e) {
        Object v = cup.get(e);
        if (v != null)
            cup.remove(e);
        return put(e);
    }

    public Object putIfAbsent(E e) {
        Object v = cup.get(e);
        if (v == null)
            v = put(e);
        return v;
    }

    public Object putIfAbsent(E e, Object tag) {
        Object v = cup.get(e);
        if (v == null)
            v = cup.put(e, tag);
        return v;
    }

    public void putAll(E... es) {
        for (E e : es) {
            cup.put(e, autoTag++);
        }
    }

    public void putAll(Map<E, Object> map) {
        cup.putAll(map);
    }

    public void remove(E e) {
        cup.remove(e);
    }

    public boolean remove(E e, Object tag) {
        Object curValue = cup.get(e);
        if (!Objects.equals(curValue, tag) ||
                (curValue == null && !cup.containsKey(e))) {
            return false;
        }
        cup.remove(e);
        return true;
    }

    public E removeByTag(Object tag) {
        Iterator<Map.Entry<E, Object>> iterator = cup.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<E, Object> mapEntry = iterator.next();
            Object value = mapEntry.getValue();
            if (Objects.equals(value, tag)) {
                E key = mapEntry.getKey();
                iterator.remove();
                return key;
            }
        }

        return null;
    }

    public E findFirst() {
        return cup.entrySet().iterator().next().getKey();
    }

    public E findLast() {
        Iterator<Map.Entry<E, Object>> iterator = cup.entrySet().iterator();

        E value = null;
        while (iterator.hasNext()) {
            value = iterator.next().getKey();
        }

        return value;
    }

    public void emptied() {
        cup.clear();
    }

    private Map<E, Object> getCup() {
        return cup;
    }

    /**
     * if u need
     */
    public Mini heart() {
        return heart.heart();
    }

    public class Mini {

        public Map<E, Object> getCup() {
            return MiniCup.this.getCup();
        }

    }

    public class CupHeart extends NoHeart<Mini> {

        MiniCup.Mini mini = new MiniCup.Mini();

        @Override
        public Mini heart() {
            return mini;
        }
    }
}
