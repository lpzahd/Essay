package com.lpzahd.common.waiter.refresh;

import com.lpzahd.atool.error.FixedError;
import com.lpzahd.common.tone.data.DataFactory;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public abstract class DspRefreshProcessor<E, D> extends RefreshProcessor implements DataFactory.DataProcess<E, D> {

    public static final int QUERY_EACH_MAX_COUNT = 20;

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

    private List<E> source;
    private List<D> data;

    private DataFactory<E, D> factory;
    private Disposable refreshDispose;

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setRefreshEnable(boolean enable) {
        this.refreshEnable = enable;
    }

    public void setPtrEnable(boolean ptrEnable) {
        this.ptrEnable = ptrEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    public DspRefreshProcessor() {
        factory = DataFactory.of(this);
    }

    protected List<E> getSource() {
        return source;
    }

    protected List<D> getData() {
        return data;
    }

    protected void refresh(Flowable<List<E>> flowable, final boolean ptr) {
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
                .map(new Function<List<E>, List<D>>() {
                    @Override
                    public List<D> apply(@NonNull List<E> es) throws Exception {
                        source = es;
                        return factory.processArray(es);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<D>>() {
                    @Override
                    public void accept(List<D> list) throws Exception {
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
