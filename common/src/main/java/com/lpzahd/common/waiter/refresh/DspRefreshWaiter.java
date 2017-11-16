package com.lpzahd.common.waiter.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.waiter.agency.WindowWaiter;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : 真实数据与显示数据分离
 */
public abstract class DspRefreshWaiter<E, D> extends WindowWaiter {

    private SwipeRefreshProcessor<E, D> processor;
    private List<E> dataSource;


    public DspRefreshWaiter(SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView) {

        processor = new SwipeRefreshProcessor<E, D>(swipeRefreshLayout, recyclerView) {

            @Override
            public D process(E e) {
                return DspRefreshWaiter.this.process(e);
            }

            @Override
            public Flowable<List<E>> doRefresh(int page) {
                return DspRefreshWaiter.this.doRefresh(page);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onPtrSuccess(@LoadState int state) {
                super.onPtrSuccess(state);
                if (state == RefreshProcessor.STATE_NO_MORE || state == RefreshProcessor.STATE_HAS_MORE) {
                    ToneAdapter adapter = (ToneAdapter) recyclerView.getAdapter();
                    adapter.setData(getData());
                    dataSource = getSource();
                    T.t("刷新%s条数据", getData().size());
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onLoadingSuccess(@LoadState int state) {
                super.onLoadingSuccess(state);
                if (state == RefreshProcessor.STATE_NO_MORE || state == RefreshProcessor.STATE_HAS_MORE) {
                    ToneAdapter adapter = (ToneAdapter) recyclerView.getAdapter();
                    adapter.addAll(getData());
                    dataSource.addAll(getSource());
                    T.t("新增%s条数据", getData().size());
                }
            }

            @Override
            public void onPtrError(int errorCode, String errorMessage) {
                super.onPtrError(errorCode, errorMessage);
                T.t(errorMessage);
            }

            @Override
            public void onLoadError(int errorCode, String errorMessage) {
                super.onLoadError(errorCode, errorMessage);
                T.t(errorMessage);
            }
        };
        processor.attach();
    }

    public void setSwipeRefreshCallBack(SwipeRefreshWaiter.SwipeRefreshCallBack callback) {
        processor.setSwipeRefreshCallBack(callback);
    }

    public void setStart(int start) {
        processor.setStart(start);
    }

    public void setCount(int count) {
        processor.setCount(count);
    }

    public int getCount() {
        return processor.getCount();
    }

    public int getPage() {
        return processor.getPage();
    }

    public List<E> getSource() {
        return dataSource;
    }

    public abstract Flowable<List<E>> doRefresh(int page);

    public abstract D process(E e);

    public SwipeRefreshProcessor getProcessor() {
        return processor;
    }

    public void autoRefresh() {
        processor.onRefresh();
    }

    @Override
    protected void destroy() {
        super.destroy();

        if (processor != null)
            processor.dispose();
    }

    private abstract static class SwipeRefreshProcessor<E, D> extends DspRefreshProcessor<E, D> implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshWaiter.LoadMoreCallBack {

        private SwipeRefreshLayout swipeRefreshLayout;

        private RecyclerView recyclerView;

        private SwipeRefreshWaiter.SwipeRefreshCallBack callback;

        public void setSwipeRefreshCallBack(SwipeRefreshWaiter.SwipeRefreshCallBack callback) {
            this.callback = callback;
        }

        private SwipeRefreshProcessor(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView) {
            this.swipeRefreshLayout = swipeRefreshLayout;
            this.recyclerView = recyclerView;
        }

        private void attach() {
            swipeRefreshLayout.setOnRefreshListener(this);
            recyclerView.addOnScrollListener(new SwipeRefreshWaiter.RecyclerViewOnScroll(recyclerView, this));
        }

        @Override
        public void onStartPtr() {
            super.onStartPtr();
            swipeRefreshLayout.setRefreshing(true);
            if (callback != null)
                callback.onStartPtr(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onPtrComplete() {
            super.onPtrComplete();
            swipeRefreshLayout.setRefreshing(false);
            if (callback != null)
                callback.onPtrComplete(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onStartLoad() {
            super.onStartLoad();
            swipeRefreshLayout.setRefreshing(true);
            if (callback != null)
                callback.onStartLoad(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onLoadComplete() {
            super.onLoadComplete();
            swipeRefreshLayout.setRefreshing(false);
            if (callback != null)
                callback.onLoadComplete(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onRefresh() {
            onStartPtr();
            refresh(doRefresh(getAdv()), true);
            if (callback != null)
                callback.onRefresh(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onLoadMore() {
            if (getLoadState() == RefreshProcessor.STATE_HAS_MORE) {
                onStartLoad();
                refresh(doRefresh(getAdv()), false);

                if (callback != null)
                    callback.onRefresh(getStart(), getAdv(), getLoadState());
            }
        }

        /**
         * 数据观察者
         *
         * @param page 操作页码
         */
        public abstract Flowable<List<E>> doRefresh(int page);

    }


}
