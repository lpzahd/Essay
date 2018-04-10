package com.lpzahd.common.tone.waiter;

import com.lpzahd.atool.action.Check;
import com.lpzahd.atool.ui.T;
import com.lpzahd.waiter.WaiterActivity;
import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.agency.WindowWaiter;
import com.lpzahd.waiter.waiter.Waiter;

import java.util.HashMap;
import java.util.Map;

public class WaiterManager {

    private static WaiterManager sManager;

    public static WaiterManager single() {
        return sManager != null ? sManager : (sManager = new WaiterManager());
    }

    private WaiterManager() {

    }

    private Map<Class, Waiter[]> mMap = new HashMap<>();

    public void put(Class clz, Waiter... waiters) {
        mMap.put(clz, waiters);
    }

    public void remove(Class clz) {
        mMap.remove(clz);
    }

    public Waiter[] get(Class clz) {
        return mMap.get(clz);
    }

    public void clear() {
        mMap.clear();
    }

}
