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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lpzahd.Lists;
import com.lpzahd.aop.api.Log;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.keeper.storage.task.RealDownloadTask;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter;
import com.lpzahd.common.waiter.refresh.RefreshProcessor;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.CollectionActivity;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.gallery.tool.MediaTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * 作者 : 迪
 * 时间 : 2018/1/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 * TODO 长按修改图片信息 和 还原数据操作
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

    private Realm mRealm;
    private RxBus.BusService mBusService;

    public CollectionWaiter(CollectionActivity collectionActivity) {
        super(collectionActivity);
    }

    @Override
    protected void init() {
        super.init();
        mRealm = Realm.getDefaultInstance();
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
                if(RefreshProcessor.hasmore(loadState))
                    totalFab.setTitle(String.format("一共%s张", mRefreshWaiter.getSource().size()));
            }

            @Override
            public void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                if(RefreshProcessor.hasmore(loadState))
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

        recyclerView.addOnItemTouchListener(
                new OnItemHolderTouchListener<LeisureWaiter.LeisureHolder>(recyclerView) {
                    @Override
                    public void onLongClick(RecyclerView rv, LeisureWaiter.LeisureHolder holder) {
                        super.onLongClick(rv, holder);
                        showEditPhotoDialog(holder.getAdapterPosition());

                    }
        });

        mBusService = new RxBus.BusService(CollectionActivity.TAG, new Receiver<Boolean>() {
            @Override
            public void receive(Flowable<Boolean> flowable) {
                flowable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if(aBoolean)
                                    mRefreshWaiter.autoRefresh();
                            }
                        });
            }
        });
        mBusService.regist();
    }

    private void showEditPhotoDialog(final int position) {
        final Collection collection = mRefreshWaiter.getSource().get(position);
        String name = new File(collection.getImage().getPath()).getName();
        new MaterialDialog.Builder(context)
                .title("还原图片")
                .content("图片" + name + "将被还原，收藏图会被移除，确定？")
                .negativeText(R.string.tip_negative)
                .positiveText(R.string.tip_positive)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {

                        boolean restore = restorePhoto(collection);
                        if(restore) {
                            mAdapter.remove(position);
                        }
                    }
                })
                .show();
    }

    private boolean restorePhoto(Collection collection) {
        String originalPath = collection.getOriginalPath();
        File originalFile = new File(originalPath);
        if(originalFile.exists() && originalFile.isFile()) {
            T.t("在%s位置已存在同文件名的文件,还原操作被取消!", originalPath);
            return false;
        }

        Image image = collection.getImage();
        Files.copy(image.getPath(), originalPath);
        mRealm.beginTransaction();
        collection.deleteFromRealm();
        mRealm.commitTransaction();

        Files.delete(image.getPath());
        return true;
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

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();

        mBusService.unregist();
    }

    private static class CollectionRefreshWaiter extends DspRefreshWaiter<Collection, LeisureWaiter.LeisureModel> {

        private Realm realm;

        CollectionRefreshWaiter(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView) {
            super(swipeRefreshLayout, recyclerView);
            setCount(Integer.MAX_VALUE);
            realm = Realm.getDefaultInstance();
        }

        @Override
        public Flowable<List<Collection>> doRefresh(int page) {
            return Flowable.just(page)
                    .map(new Function<Integer, List<Collection>>() {
                        @Override
                        @Log
                        public List<Collection> apply(Integer integer) throws Exception {
                            List<Collection> collections = realm.where(Collection.class)
                                    .findAll();

                            if(Lists.empty(collections))
                                collections = Collections.emptyList();

                            return collections;
                        }
                    });
        }

        @Override
        public LeisureWaiter.LeisureModel process(Collection collection) {
            Image image = collection.getImage();
            LeisureWaiter.LeisureModel model = new LeisureWaiter.LeisureModel();
            model.uri = Frescoer.uri(image.getPath(), ImageSource.SOURCE_FILE);
            model.width = image.getWidth();
            model.height = image.getHeight();
            return model;
        }

        @Override
        protected void destroy() {
            super.destroy();
            if(!realm.isClosed())
                realm.close();
        }
    }
}
