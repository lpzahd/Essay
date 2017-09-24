package com.lpzahd.common.waiter.refresh;

import com.lpzahd.atool.error.FixedError;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class RxRefreshProcessor extends RefreshProcessor {

    private static final int QUERY_EACH_MAX_COUNT = 20;

    /**
     * 是否开启下拉刷新
     */
    protected boolean isPtrEnabled() {
        return true;
    }

    /**
     * 是否开启加载更多
     */
    protected boolean isLoadMoreEnabled() {
        return true;
    }

    private boolean refreshEnable = true;

    private boolean ptrEnable = true;

    private boolean loadMoreEnable = true;

    private int count = QUERY_EACH_MAX_COUNT;

    private List<?> data;

    private Disposable refreshDispose;

    public void setRefreshEnable(boolean enable) {
        this.refreshEnable = enable;
    }

    public void setPtrEnable(boolean ptrEnable) {
        this.ptrEnable = ptrEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    protected List<?> getData() {
        return data;
    }

    protected void refresh(Flowable<? extends List> flowable, final boolean ptr) {
        if (flowable == null) return;

        if (!refreshEnable) return;

        if (ptr) {
            // 下拉刷新
            if (!isPtrEnabled()) return;
            if (!ptrEnable) return;
        } else {
            // 加载刷新
            if (!isLoadMoreEnabled()) return;
            if (!loadMoreEnable) return;
        }

        dispose();

        refreshDispose = flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List>() {
                    @Override
                    public void accept(List list) throws Exception {
                        data = list;
                        if (ptr) {
                            if (list == null || list.isEmpty()) {
                                onPtrSuccess(STATE_EMPTY);
                            } else if (list.size() < count) {
                                onPtrSuccess(STATE_NO_MORE);
                            } else {
                                onPtrSuccess(STATE_HAS_MORE);
                            }
                            onPtrComplete();
                        } else {
                            if (list == null || list.isEmpty()) {
                                onLoadingSuccess(STATE_EMPTY);
                            } else if (list.size() < count) {
                                onLoadingSuccess(STATE_NO_MORE);
                            } else {
                                onLoadingSuccess(STATE_HAS_MORE);
                            }
                            onLoadComplete();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        FixedError error = FixedError.FixedErrorHandler.handleError(throwable);
                        if (ptr) {
                            onPtrError(error.getCode(), error.getMessage());
                            onPtrComplete();
                        } else {
                            onLoadError(error.getCode(), error.getMessage());
                            onLoadComplete();
                        }
                    }
                });

    }

    public void dispose() {
        if (refreshDispose != null && !refreshDispose.isDisposed()) {
            refreshDispose.dispose();
        }
    }
}
