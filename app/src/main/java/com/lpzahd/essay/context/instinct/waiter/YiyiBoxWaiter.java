package com.lpzahd.essay.context.instinct.waiter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lpzahd.Lists;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter;
import com.lpzahd.common.waiter.refresh.RefreshProcessor;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.instinct.InstinctActivity;
import com.lpzahd.essay.context.instinct.InstinctPhotoActivity;
import com.lpzahd.essay.context.instinct.yiyibox.YiyiBox;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.essay.exotic.retrofit.Net;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者 : 迪
 * 时间 : 2017/10/27.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiBoxWaiter extends ToneActivityWaiter<InstinctActivity> implements View.OnClickListener {

    private static final int QUERY_COUNT = 366;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tool_bar_layout)
    FrameLayout toolBarLayout;
    @BindView(R.id.toggle_fab)
    FloatingActionButton toggleFab;
    @BindView(R.id.page_fab)
    FloatingActionButton pageFab;
    @BindView(R.id.random_page_fab)
    FloatingActionButton randomPageFab;
    @BindView(R.id.current_page_fab)
    FloatingActionButton currentPageFab;
    @BindView(R.id.total_page_fab)
    FloatingActionButton totalPageFab;
    @BindView(R.id.menu_fab)
    FloatingActionsMenu menuFab;

    private DspRefreshWaiter<YiyiBox.DataBean.ItemsBean, LeisureWaiter.LeisureModel> mRefreshWaiter;

    LeisureWaiter.LeisureAdapter mAdapter;

    public YiyiBoxWaiter(InstinctActivity instinctActivity) {
        super(instinctActivity);
    }

    @Override
    protected void initView() {
        toolBar.setTitle("荷尔蒙");
        context.setSupportActionBar(toolBar);

        toggleFab.setOnClickListener(this);
        pageFab.setOnClickListener(this);
        randomPageFab.setOnClickListener(this);


        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new LeisureWaiter.LeisureAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<LeisureWaiter.LeisureHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, LeisureWaiter.LeisureHolder leisureHolder) {
                InstinctPhotoActivity.startActivity(context);
                final int position = leisureHolder.getAdapterPosition();
                RxTaxi.get().regist(YiyiBoxPhotoWaiter.TAG, new Transmitter() {
                    @Override
                    public Flowable<YiyiBox.DataBean.ItemsBean> transmit() {
                        return Flowable.just(mRefreshWaiter.getSource()
                                .get(position));
                    }
                });
            }
        });

        addWindowWaiter(mRefreshWaiter = new DspRefreshWaiter<YiyiBox.DataBean.ItemsBean, LeisureWaiter.LeisureModel>(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<List<YiyiBox.DataBean.ItemsBean>> doRefresh(int page) {
                return Net.get().yiyiBoxImg(page + 1)
                        .zipWith(Net.get().yiyiBoxImg2(page + 1), new BiFunction<YiyiBox, YiyiBox, YiyiBox>() {
                            @Override
                            public YiyiBox apply(@NonNull YiyiBox yiyiBox, @NonNull YiyiBox yiyiBox2) throws Exception {
                                if(yiyiBox == null || yiyiBox.getData() == null || Lists.empty(yiyiBox.getData().getItems())
                                        || yiyiBox2 == null || yiyiBox2.getData() == null || Lists.empty(yiyiBox2.getData().getItems()))
                                    return null;

                                yiyiBox.getData().getItems().addAll(yiyiBox2.getData().getItems());
                                return yiyiBox;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .map(new Function<YiyiBox, List<YiyiBox.DataBean.ItemsBean>>() {
                            @Override
                            public List<YiyiBox.DataBean.ItemsBean> apply(@NonNull YiyiBox yiyiBox) throws Exception {
                                if (yiyiBox == null
                                        || yiyiBox.getData() == null
                                        || Lists.empty(yiyiBox.getData().getItems()))
                                    return Collections.emptyList();
                                return yiyiBox.getData().getItems();
                            }
                        });
            }

            @Override
            public LeisureWaiter.LeisureModel process(YiyiBox.DataBean.ItemsBean itemsBean) {
                LeisureWaiter.LeisureModel model = new LeisureWaiter.LeisureModel();
                model.width = itemsBean.getWidth();
                model.height = itemsBean.getHeight();
                model.uri = Frescoer.uri("http:" + itemsBean.getImg(), ImageSource.SOURCE_NET);

                if (itemsBean.getShorturl().startsWith("v")) {
                    //video
                    model.tag = "视频";
                }

                return model;
            }

        });

        mRefreshWaiter.setSwipeRefreshCallBack(new SwipeRefreshWaiter.SimpleCallBack() {
            @Override
            public void onPtrComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                refreshPageFab(page + 1, page - start + 1);
            }

            @Override
            public void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                refreshPageFab(page + 1, page - start + 1);
            }
        });

        refreshPageFab(0, 0);
        mRefreshWaiter.autoRefresh();
    }

    private void refreshPageFab(int page, int total) {
        currentPageFab.setTitle("当前是第" + page + "页");
        totalPageFab.setTitle("当前一共看了" + total + "页");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_fab:
                toggleShowModel();
                break;
            case R.id.page_fab:
                searchSkipPage();
                break;
            case R.id.random_page_fab:
                searchRandomPage();
                break;
        }
    }

    private void toggleShowModel() {
        menuFab.collapse();
        //这里还是用门面模式好，先懒得写
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager == null) return;

        if (manager instanceof StaggeredGridLayoutManager) {
            mAdapter.reloadTag();
            int[] lastPositions = new int[((StaggeredGridLayoutManager) manager).getSpanCount()];
            int firstVisibleItem = ((StaggeredGridLayoutManager) manager).findFirstVisibleItemPositions(lastPositions)[0];
            manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            manager.scrollToPosition(firstVisibleItem);
            return;
        }

        if (manager instanceof LinearLayoutManager) {
            mAdapter.reloadTag();
            int firstVisibleItem = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            manager.scrollToPosition(firstVisibleItem);
            return;
        }
    }


    private void searchSkipPage() {
        new MaterialDialog.Builder(context)
                .title("指定页码搜索")
                .inputRange(0, QUERY_COUNT)
                .autoDismiss(false)
                .input("跳转页码", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@android.support.annotation.NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            int page = Integer.parseInt(input.toString());
                            if (page < 0) {
                                page = -page;
                            } else if (page == 0) {
                                page = 1;
                            }

                            if (page > 30) {
                                page = page % 30;
                            }
                            mRefreshWaiter.setStart(page - 1);
                            mRefreshWaiter.autoRefresh();
                            recyclerView.getLayoutManager().scrollToPosition(0);
                            dialog.dismiss();
                        } catch (NumberFormatException ex) {
                            T.t("请输入正确的数字");
                        }
                    }
                })
                .show();
    }


    private void searchRandomPage() {
        Random random = new Random();
        mRefreshWaiter.setStart(random.nextInt(QUERY_COUNT));
        mRefreshWaiter.autoRefresh();
        recyclerView.getLayoutManager().scrollToPosition(0);
    }

    @Override
    protected void destroy() {
        RxTaxi.get().unregist(YiyiBoxPhotoWaiter.TAG);
    }
}
