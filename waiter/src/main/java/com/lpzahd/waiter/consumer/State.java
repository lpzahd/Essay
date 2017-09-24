package com.lpzahd.waiter.consumer;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author : Lpzahd
 * Date : 八月
 * Desction : (•ิ_•ิ)
 */
public interface State {

    /**
     * 不拦截子waiter操作,结果由子waiter操作
     */
    int STATE_IGNORE = 0;

    /**
     * 拦截子waiter操作,超类(Activity)方法不再调用
     */
    int STATE_PREVENT = 1;

    /**
     * 拦截子waiter操作，超类(Activity)方法调用
     */
    int STATE_ALLOW = 2;

    @IntDef({STATE_IGNORE, STATE_PREVENT, STATE_ALLOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InnerState {
    }

}
