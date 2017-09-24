package com.lpzahd.common.waiter.refresh;

/**
 * Created by Lpzahd on 2016/11/29 0029.
 * <p>
 * 下拉刷新
 */

public interface IPullToRefresh {

    void onStartPtr();

    void onPtring();

    void onPtrSuccess(@RefreshProcessor.LoadState int state);

    void onPtrError(int errorCode, String errorMessage);

    void onPtrComplete();

}
