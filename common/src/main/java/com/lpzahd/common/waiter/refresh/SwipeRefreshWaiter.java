package com.lpzahd.common.waiter.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.waiter.agency.WindowWaiter;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : swiperefreshlayout + recyclerview 刷新处理器
 */
public abstract class SwipeRefreshWaiter extends WindowWaiter {

    private SwipeRefreshProcessor processor;

    public SwipeRefreshWaiter(SwipeRefreshLayout swipeRefreshLayout,final RecyclerView recyclerView) {
        processor = new SwipeRefreshProcessor(swipeRefreshLayout, recyclerView) {
            @Override
            public Flowable<? extends List> doRefresh(int page) {
                return SwipeRefreshWaiter.this.doRefresh(page);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onPtrSuccess(@LoadState int state) {
                super.onPtrSuccess(state);
                if(state == RefreshProcessor.STATE_NO_MORE || state == RefreshProcessor.STATE_HAS_MORE) {
                    ToneAdapter adapter = (ToneAdapter) recyclerView.getAdapter();
                    adapter.setData(getData());
                    T.t("刷新%s条数据", getData().size());
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onLoadingSuccess(@LoadState int state) {
                super.onLoadingSuccess(state);
                if(state == RefreshProcessor.STATE_NO_MORE || state == RefreshProcessor.STATE_HAS_MORE) {
                    ToneAdapter adapter = (ToneAdapter) recyclerView.getAdapter();
                    adapter.addAll(getData());
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

    public void setSwipeRefreshCallBack(SwipeRefreshCallBack callback) {
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

    public abstract Flowable<? extends List> doRefresh(int page);

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

    public abstract static class DataFlowable {
        public abstract Flowable<? extends List> doRefresh(int page);

        public int getPage() {
            return RxRefreshProcessor.QUERY_EACH_MAX_COUNT;
        }
    }

    private abstract static class SwipeRefreshProcessor extends RxRefreshProcessor implements SwipeRefreshLayout.OnRefreshListener, LoadMoreCallBack {

        private SwipeRefreshLayout swipeRefreshLayout;

        private RecyclerView recyclerView;

        private SwipeRefreshCallBack callback;

        public void setSwipeRefreshCallBack(SwipeRefreshCallBack callback) {
            this.callback = callback;
        }

        private SwipeRefreshProcessor(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView) {
            this.swipeRefreshLayout = swipeRefreshLayout;
            this.recyclerView = recyclerView;
        }

        private void attach() {
            swipeRefreshLayout.setOnRefreshListener(this);
            recyclerView.addOnScrollListener(new RecyclerViewOnScroll(recyclerView, this));
        }

        @Override
        public void onStartPtr() {
            super.onStartPtr();
            swipeRefreshLayout.setRefreshing(true);
            if(callback != null)
                callback.onStartPtr(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onPtrComplete() {
            super.onPtrComplete();
            swipeRefreshLayout.setRefreshing(false);
            if(callback != null)
                callback.onPtrComplete(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onStartLoad() {
            super.onStartLoad();
            swipeRefreshLayout.setRefreshing(true);
            if(callback != null)
                callback.onStartLoad(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onLoadComplete() {
            super.onLoadComplete();
            swipeRefreshLayout.setRefreshing(false);
            if(callback != null)
                callback.onLoadComplete(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onRefresh() {
            onStartPtr();
            refresh(doRefresh(getAdv()), true);
            if(callback != null)
                callback.onRefresh(getStart(), getAdv(), getLoadState());
        }

        @Override
        public void onLoadMore() {
            if (getLoadState() == RefreshProcessor.STATE_HAS_MORE) {
                onStartLoad();
                refresh(doRefresh(getAdv()), false);

                if(callback != null)
                    callback.onRefresh(getStart(), getAdv(), getLoadState());
            }
        }

        /**
         * 数据观察者
         *
         * @param page 操作页码
         */
        public abstract Flowable<? extends List> doRefresh(int page);

    }

    public interface SwipeRefreshCallBack {
        void onStartPtr(int start, int page, @RefreshProcessor.LoadState int loadState);
        void onPtrComplete(int start, int page, @RefreshProcessor.LoadState int loadState);
        void onStartLoad(int start, int page, @RefreshProcessor.LoadState int loadState);
        void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState);
        void onRefresh(int start, int page, @RefreshProcessor.LoadState int loadState);
    }

    public static class SimpleCallBack implements SwipeRefreshCallBack {

        @Override
        public void onStartPtr(int start, int page, @RefreshProcessor.LoadState int loadState) {

        }

        @Override
        public void onPtrComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {

        }

        @Override
        public void onStartLoad(int start, int page, @RefreshProcessor.LoadState int loadState) {

        }

        @Override
        public void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {

        }

        @Override
        public void onRefresh(int start, int page, @RefreshProcessor.LoadState int loadState) {

        }
    }

    /**
     * 加载更多回调
     */
    interface LoadMoreCallBack {
        void onLoadMore();
    }

    static class RecyclerViewOnScroll extends RecyclerView.OnScrollListener {

        private static final long MIN_DURATION_TIME = 3000;

        RecyclerView mRecyclerView;

        LoadMoreCallBack mLoadMoreCallBack;

        int lastVisibleItem = 0;

        int firstVisibleItem = 0;

        @RecyclerView.Orientation
        int orientation;

        long loadmoreTime;

        public RecyclerViewOnScroll(RecyclerView recyclerView, LoadMoreCallBack loadMoreCallBack) {
            this.mRecyclerView = recyclerView;
            this.mLoadMoreCallBack = loadMoreCallBack;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager manager = ((LinearLayoutManager) layoutManager);
                lastVisibleItem = manager.findLastVisibleItemPosition();
                firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
                orientation = manager.getOrientation();
            }

            else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager manager = ((StaggeredGridLayoutManager) layoutManager);
                int[] pos = new int[manager.getSpanCount()];
                manager.findLastVisibleItemPositions(pos);
                lastVisibleItem = findMax(pos);
                firstVisibleItem = manager.findFirstVisibleItemPositions(pos)[0];
                orientation = manager.getOrientation();
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();

            //这个就是判断当前滑动停止了，并且获取当前屏幕最后一个可见的条目是第几个，当前屏幕数据已经显示完毕的时候就去加载数据
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                // 说明查询的数量有点少了，都没有回收
                if (firstVisibleItem == 0) {
                    L.e("最好检查一下是不是查询的数量太少了[SwipeRefreshWaiter.RecyclerViewOnScroll.onScrollStateChanged]");
                    return;
                }

                // 验证是否能铺满屏幕
                int scrollExtent = 0;
                // recyclerView 的高度
                int scrollRange = 0;
                if(orientation == RecyclerView.VERTICAL) {
                    // 区域高度
                    scrollExtent = recyclerView.computeVerticalScrollExtent();
                    // recyclerView 的高度
                    scrollRange = recyclerView.computeVerticalScrollRange();

                } else if(orientation == RecyclerView.HORIZONTAL) {
                    // 区域宽度
                    scrollExtent = recyclerView.computeHorizontalScrollExtent();
                    // recyclerView 的宽度
                    scrollRange = recyclerView.computeHorizontalScrollRange();
                }

                if (scrollRange > scrollExtent) {

                    // 延时加载
                    long time = System.currentTimeMillis() - loadmoreTime;
                    if (time > MIN_DURATION_TIME) {
                        //回调加载更多
                        mLoadMoreCallBack.onLoadMore();
                        loadmoreTime = System.currentTimeMillis();
                    }
                }


            }
        }

        //找到数组中的最大值
        private int findMax(int[] lastPositions) {

            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }
}
