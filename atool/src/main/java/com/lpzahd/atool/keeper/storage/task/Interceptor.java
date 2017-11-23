package com.lpzahd.atool.keeper.storage.task;

import com.lpzahd.atool.keeper.storage.Request;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface Interceptor {

    boolean intercept(int index, Request.SingleTask task, okhttp3.Request request);

}
