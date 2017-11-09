package com.lpzahd.atool.keeper.storage;

import okhttp3.Request;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface Interceptor {

    boolean intercept(int index, Config.SingleTask task, Request request);

}
