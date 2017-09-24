package com.lpzahd.common.waiter.refresh;

/**
 * Created by Lpzahd on 2016/11/29 0029.
 * <p>
 * 加载更多
 */

public interface ILoadMore {

    // 开始加载
    void onStartLoad();

    // 正在加载
    void onLoading();

    // 成功加载结束
    void onLoadingSuccess(@RefreshProcessor.LoadState int state);

    // 错误加载
    void onLoadError(int errorCode, String errorMessage);

    // 加载完毕
    void onLoadComplete();

}
