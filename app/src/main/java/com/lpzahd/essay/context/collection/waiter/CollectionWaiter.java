package com.lpzahd.essay.context.collection.waiter;

import android.media.ExifInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
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
import com.lpzahd.atool.keeper.storage.task.RealDownloadTask;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter;
import com.lpzahd.common.waiter.refresh.RefreshProcessor;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.CollectionActivity;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者 : 迪
 * 时间 : 2018/1/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class CollectionWaiter extends ToneActivityWaiter<CollectionActivity> implements View.OnClickListener {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tool_bar_layout)
    FrameLayout toolBarLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.menu_fab)
    FloatingActionsMenu menuFab;
    @BindView(R.id.toggle_fab)
    FloatingActionButton toggleFab;
    @BindView(R.id.index_scroll_fab)
    FloatingActionButton indexScrollFab;
    @BindView(R.id.random_scroll_fab)
    FloatingActionButton randomScrollFab;
    @BindView(R.id.current_fab)
    FloatingActionButton currentFab;
    @BindView(R.id.total_fab)
    FloatingActionButton totalFab;

    private LeisureWaiter.LeisureAdapter mAdapter;
    private CollectionRefreshWaiter mRefreshWaiter;

    public CollectionWaiter(CollectionActivity collectionActivity) {
        super(collectionActivity);
    }

    @Override
    protected void initView() {
        super.initView();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.HORIZONTAL));
        mAdapter = new LeisureWaiter.LeisureAdapter(context, OrientationHelper.HORIZONTAL);
        recyclerView.setAdapter(mAdapter);

        mRefreshWaiter = new CollectionRefreshWaiter(swipeRefreshLayout, recyclerView);
        mRefreshWaiter.autoRefresh();

        toggleFab.setOnClickListener(this);
        indexScrollFab.setOnClickListener(this);
        randomScrollFab.setOnClickListener(this);

        mRefreshWaiter.setSwipeRefreshCallBack(new SwipeRefreshWaiter.SimpleCallBack() {
            @Override
            public void onPtrComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                totalFab.setTitle(String.format("一共%s张", mRefreshWaiter.getSource().size()));
            }

            @Override
            public void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                totalFab.setTitle(String.format("一共%s张", mRefreshWaiter.getSource().size()));
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = 0;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = ((LinearLayoutManager) layoutManager);
                        firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager manager = ((StaggeredGridLayoutManager) layoutManager);
                        int[] pos = new int[manager.getSpanCount()];
                        firstVisibleItem = manager.findFirstVisibleItemPositions(pos)[0];
                    }

                    currentFab.setTitle(String.format("当前浏览到第%s张", firstVisibleItem));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_fab:
                toggleShowModel();
                break;
            case R.id.index_scroll_fab:
                scrollToPosition();
                break;
            case R.id.random_scroll_fab:
                randomScroll();
                break;
        }
    }

    private void toggleShowModel() {
        menuFab.collapse();
        mAdapter.toggle(recyclerView);
    }

    private void scrollToPosition() {
        if(mRefreshWaiter == null)
            T.t("初始化中...");
        if(Lists.empty(mRefreshWaiter.getSource()))
            T.t("没有查询到数据!");

        final int size = mRefreshWaiter.getSource().size();
        new MaterialDialog.Builder(context)
                .title("指定位置滚动")
                .inputRange(0, String.valueOf(size).length())
                .autoDismiss(false)
                .input("滚动位置", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@android.support.annotation.NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            int position = Integer.parseInt(input.toString());
                            if (position < 0) {
                                position = 0;
                            }
                            recyclerView.getLayoutManager().scrollToPosition(position);
                            dialog.dismiss();
                        } catch (NumberFormatException ex) {
                            T.t("请输入正确的数字");
                        }
                    }
                })
                .show();
    }


    private void randomScroll() {
        if(mRefreshWaiter == null)
            T.t("初始化中...");
        if(Lists.empty(mRefreshWaiter.getSource()))
            T.t("没有查询到数据!");

        final int size = mRefreshWaiter.getSource().size();
        Random random = new Random();
        recyclerView.getLayoutManager().scrollToPosition(random.nextInt(size));
    }

    private static class CollectionRefreshWaiter extends DspRefreshWaiter<LeisureWaiter.LeisureModel, LeisureWaiter.LeisureModel> {

        CollectionRefreshWaiter(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView) {
            super(swipeRefreshLayout, recyclerView);
            setCount(Integer.MAX_VALUE);
        }

        @Override
        public Flowable<List<LeisureWaiter.LeisureModel>> doRefresh(int page) {
            return Flowable.just(new File(RealDownloadTask.getPhotoDefaultPath()))
                    .filter(new Predicate<File>() {
                        @Override
                        public boolean test(File file) throws Exception {
                            return file.exists();
                        }
                    })
                    .map(new Function<File, File[]>() {
                        @Override
                        public File[] apply(File file) throws Exception {
                            return file.listFiles();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .map(new Function<File[], List<LeisureWaiter.LeisureModel>>() {
                        @Override
                        public List<LeisureWaiter.LeisureModel> apply(File[] files) throws Exception {
                            if(Lists.empty(files)) return Collections.emptyList();

                            List<LeisureWaiter.LeisureModel> models = new ArrayList<>(files.length);
                            for (File file : files) {
                                String path = file.getAbsolutePath();

                                LeisureWaiter.LeisureModel model = new LeisureWaiter.LeisureModel();
                                model.uri = Frescoer.uri(path, ImageSource.SOURCE_FILE);
                                try {
                                    ExifInterface exifInterface = new ExifInterface(path);
                                    int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                                    int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                                    model.width = width;
                                    model.height = height;

//                                    String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                                } catch (IOException e) {
                                    L.e(e.getMessage());
                                }
                                models.add(model);
                            }
                            return models;
                        }
                    });
        }

        @Override
        public LeisureWaiter.LeisureModel process(LeisureWaiter.LeisureModel model) {
            return model;
        }

    }
}
