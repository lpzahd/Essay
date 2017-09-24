package com.lpzahd.common.waiter.refresh;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class RefreshProcessor implements IPullToRefresh, ILoadMore {

    // 正在刷新
    protected static final int STATE_REFRESHING = 0;

    // 空闲
    protected static final int STATE_REFRESH_FREE = 1;

    protected static final int STATE_EMPTY = 0;
    protected static final int STATE_NO_MORE = 1;
    protected static final int STATE_HAS_MORE = 2;
    protected static final int STATE_ERROR = 3;

    @IntDef({STATE_EMPTY, STATE_NO_MORE, STATE_HAS_MORE, STATE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadState { }

    private static final int DEFAULT_PAGE_START_NUMBSER = 0;

    // 初始页码
    private int start = DEFAULT_PAGE_START_NUMBSER;

    // 当前页码
    private int page = DEFAULT_PAGE_START_NUMBSER;

    // 执行页码
    private int adv = DEFAULT_PAGE_START_NUMBSER;

    @LoadState
    private int loadState = STATE_HAS_MORE;

    private int refreshState = STATE_REFRESH_FREE;

    @IntDef({STATE_REFRESHING, STATE_REFRESH_FREE})
    @Retention(RetentionPolicy.SOURCE)
    protected @interface RefreshState {}

    @Override
    public void onStartPtr() {
        adv = start;
        loadState = STATE_HAS_MORE;
    }

    @Override
    public void onPtring() {
        refreshState = STATE_REFRESHING;
    }

    @Override
    public void onPtrSuccess(@LoadState int state) {
        loadState = state;

        if(state == STATE_EMPTY)
            adv = page;
        else
            page = adv;
    }

    @Override
    public void onPtrError(int errorCode, String errorMessage) {
        loadState = STATE_ERROR;
        adv = page;
    }

    @Override
    public void onPtrComplete() {
        refreshState = STATE_REFRESH_FREE;
    }

    @Override
    public void onStartLoad() {
        adv++;
        loadState = STATE_HAS_MORE;
    }

    @Override
    public void onLoading() {
        refreshState = STATE_REFRESHING;
    }

    @Override
    public void onLoadingSuccess(@LoadState int state) {
        loadState = state;

        if(state == STATE_EMPTY)
            adv = page;
        else
            page = adv;
    }

    @Override
    public void onLoadError(int errorCode, String errorMessage) {
        loadState = STATE_ERROR;
        adv = page;
    }

    @Override
    public void onLoadComplete() {
        refreshState = STATE_REFRESH_FREE;
    }

    /**
     * 设置初始页码
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * 获取当前页码
     */
    public int getPage() {
        return page;
    }

    /**
     * 获取即将操作的页码
     */
    public int getAdv() {
        return adv;
    }

    /**
     * 获取当前数据状态
     */
    @LoadState
    public int getLoadState() {
        return loadState;
    }

    /**
     * 获取当前刷新状态
     */
    @RefreshState
    public int getRefreshState() {
        return refreshState;
    }
}
