package com.lpzahd.essay.context.leisure.waiter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.DspRefreshWaiter;
import com.lpzahd.common.waiter.refresh.RefreshProcessor;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.leisure.LeisureActivity;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;
import com.lpzahd.essay.context.preview.SinglePicActivity;
import com.lpzahd.essay.db.leisure.WordQuery;
import com.lpzahd.essay.exotic.retrofit.Net;
import com.lpzahd.essay.tool.DateTime;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class LeisureWaiter extends ToneActivityWaiter<LeisureActivity> implements View.OnClickListener, MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {

    /**
     * 默认查询数据30条i
     */
    static final int QUERY_COUNT = 30;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.tool_bar_layout)
    FrameLayout toolBarLayout;
    @BindView(R.id.toggle_fab)
    FloatingActionButton toggleFab;
    @BindView(R.id.page_fab)
    FloatingActionButton pageFab;
    @BindView(R.id.random_wordquery_fab)
    FloatingActionButton randomWordqueryFab;
    @BindView(R.id.random_page_fab)
    FloatingActionButton randomPageFab;
    @BindView(R.id.random_word_fab)
    FloatingActionButton randomWordFab;
    @BindView(R.id.current_page_fab)
    FloatingActionButton currentPageFab;
    @BindView(R.id.total_page_fab)
    FloatingActionButton totalPageFab;
    @BindView(R.id.menu_fab)
    FloatingActionsMenu menuFab;
    @BindView(R.id.activity_leisure)
    FrameLayout activityLeisure;

    private DspRefreshWaiter<BaiduPic.ImgsBean, LeisureModel> mRefreshWaiter;
    private LeisureAdapter mAdapter;

    // 查询关键字
    private String mWord = "萝莉";

    private Realm mRealm;

    public LeisureWaiter(LeisureActivity leisureActivity) {
        super(leisureActivity);
    }

    @Override
    protected void init() {
        super.init();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public int createOptionsMenu(Menu menu) {
        context.getMenuInflater().inflate(R.menu.menu_leisure, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return super.createOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_fab:
                toggleShowModel();
                break;
            case R.id.random_wordquery_fab:
                searchRandomWordQuery();
                break;
            case R.id.page_fab:
                searchSkipPage();
                break;
            case R.id.random_word_fab:
                searchRandomWord();
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
        if(manager == null) return ;

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

    private void searchRandomWordQuery() {
        RealmResults<WordQuery> queries = mRealm.where(WordQuery.class)
                .findAll();

        if (Lists.empty(queries)) {
            T.t("搜索库暂无内容");
            return;
        }

        Random random = new Random();
        mWord = queries.get(random.nextInt(queries.size())).word;
        toolBar.setTitle(mWord);
        mRefreshWaiter.autoRefresh();
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
                            if(page < 0) {
                                page = -page;
                            } else if(page == 0) {
                                page = 1;
                            }

                            if(page > 30) {
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

    private void searchRandomWord() {
        Random random = new Random();
        char[] captcha = new char[random.nextInt(4) + 1];
        for (int i = 0; i < captcha.length; i++) {
            captcha[i] = (char) (random.nextInt(0x53E3) + 0x559D);
        }
        mWord = new String(captcha);

        toolBar.setTitle(mWord);
        mRefreshWaiter.autoRefresh();
    }

    private void searchRandomPage() {
        Random random = new Random();
        mRefreshWaiter.setStart(random.nextInt(QUERY_COUNT));
        mRefreshWaiter.autoRefresh();
        recyclerView.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Strings.empty(query))
            return false;

        mWord = query;
        toolBar.setTitle(mWord);

        // 下拉刷新
        mRefreshWaiter.autoRefresh();

        final WordQuery findWord = mRealm.where(WordQuery.class)
                .equalTo("word", mWord)
                .findFirst();

        if (findWord == null) {
            // 保存查询关键字
            final WordQuery wordQuery = new WordQuery();
            wordQuery.count = 1;
            wordQuery.word = query;
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(wordQuery);
                }
            });
        } else {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    findWord.count++;
                    findWord.date = DateTime.now();
                }
            });

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }

    static class LeisureModel {
        Uri uri;
        int width;
        int height;
    }

    @Override
    protected void initView() {
        super.initView();
        setupView();

        toggleFab.setOnClickListener(this);
        pageFab.setOnClickListener(this);
        randomWordqueryFab.setOnClickListener(this);
        randomWordFab.setOnClickListener(this);
        randomPageFab.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(this);

        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new LeisureAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<LeisureHolder>(recyclerView) {
            @Override
            public void onLongClick(RecyclerView rv, LeisureHolder leisureHolder) {
                SinglePicActivity.startActivity(context);
                final int position = leisureHolder.getAdapterPosition();
                RxTaxi.get().regist(SinglePicActivity.TAG, new Transmitter() {
                    @Override
                    public Flowable transmit() {
                        return Flowable.just(mRefreshWaiter.getSource()
                                .get(position).getObjURL());
                    }
                });
            }
        });

        addWindowWaiter(mRefreshWaiter = new DspRefreshWaiter<BaiduPic.ImgsBean, LeisureModel>(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<List<BaiduPic.ImgsBean>> doRefresh(int page) {
                return Net.get().baiduImg(mWord, page, getCount())
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .map(new Function<BaiduPic, List<BaiduPic.ImgsBean>>() {
                            @Override
                            public List<BaiduPic.ImgsBean> apply(@NonNull BaiduPic baiduPic) throws Exception {
                                if (baiduPic == null || Lists.empty(baiduPic.getImgs()))
                                    return Collections.emptyList();
                                return baiduPic.getImgs();
                            }
                        });
            }

            @Override
            public LeisureModel process(BaiduPic.ImgsBean bean) {
                LeisureModel model = new LeisureModel();
                model.width = bean.getWidth();
                model.height = bean.getHeight();
                model.uri = Frescoer.uri(bean.getMiddleURL(), ImageSource.SOURCE_NET);
                return model;
            }
        });
//        addWindowWaiter(mRefreshWaiter = new SwipeRefreshWaiter(swipeRefreshLayout, recyclerView) {
//
//            @Override
//            public Flowable<? extends List> doRefresh(final int page) {
//                return Net.get().baiduImg(mWord, page, getCount())
//                        .toFlowable(BackpressureStrategy.BUFFER)
//                        .map(new Function<BaiduPic, List>() {
//                            @Override
//                            public List apply(@NonNull BaiduPic pic) throws Exception {
//                                if (pic == null || Lists.empty(pic.getImgs()))
//                                    return Collections.emptyList();
//
//                                List<BaiduPic.ImgsBean> imgs = pic.getImgs();
//                                List<LeisureModel> leisures = new ArrayList<>(imgs.size());
//                                for (int i = 0, size = imgs.size(); i < size; i++) {
//                                    BaiduPic.ImgsBean bean = imgs.get(i);
//                                    LeisureModel model = new LeisureModel();
//                                    model.width = bean.getWidth();
//                                    model.height = bean.getHeight();
//                                    model.uri = Frescoer.uri(bean.getMiddleURL(), ImageSource.SOURCE_NET);
//                                    leisures.add(model);
//                                }
//                                return leisures;
//                            }
//                        });
//            }
//
//        });

        mRefreshWaiter.setCount(QUERY_COUNT);

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

    private void setupView() {
        RealmResults<WordQuery> query = mRealm.where(WordQuery.class)
                .findAllSorted("date", Sort.DESCENDING);
        if (query != null && query.size() != 0) {

            String[] suggestions = new String[query.size()];
            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i] = query.get(i).word;
            }

            searchView.setSuggestions(suggestions);

            mWord = query.first().word;
        }

        toolBar.setTitle(mWord);
        context.setSupportActionBar(toolBar);
    }

    @Override
    protected void destroy() {
        super.destroy();

        if (!mRealm.isClosed())
            mRealm.close();

        RxTaxi.get().unregist(SinglePicActivity.TAG);

    }

    static class LeisureHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.simple_drawee_view)
        SimpleDraweeView simpleDraweeView;

        @BindView(R.id.ripple)
        RippleView ripple;

        LeisureHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class LeisureAdapter extends ToneAdapter<LeisureModel, LeisureHolder> {

        int IMG_SIZE_WIDTH;
        int IMG_SIZE_HEIGH;

        boolean STYLE_OPEN_RESIZE = false;

        LeisureAdapter(Context context) {
            super(context);
            IMG_SIZE_HEIGH = Ui.dip2px(context, 160);
        }

        void reloadTag() {
            IMG_SIZE_WIDTH = -1;
        }


        @Override
        public LeisureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LeisureHolder(inflateItemView(R.layout.item_leisure, parent));
        }

        @Override
        public void onBindViewHolder(final LeisureHolder holder, int position) {

            final LeisureModel model = getItem(position);
            displayDraweeView(holder.simpleDraweeView, model);
        }

        private void displayDraweeView(final SimpleDraweeView v, final LeisureModel model) {
            if (IMG_SIZE_WIDTH == 0)
                IMG_SIZE_WIDTH = v.getWidth();

            if (IMG_SIZE_WIDTH <= 0) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        IMG_SIZE_WIDTH = v.getWidth();
                        setLayout(v, model);
                    }
                });
                return;
            }

            setLayout(v, model);
        }

        private void setLayout(SimpleDraweeView v, LeisureModel model) {
            ViewGroup.LayoutParams pamras = v.getLayoutParams();

            final int w = model.width;
            final int h = model.height;
            if (w <= 0 || h <= 0) {
                pamras.height = IMG_SIZE_HEIGH;

            } else {
                pamras.height = (int) ((float) IMG_SIZE_WIDTH * (float) h / (float) w);
            }

            v.setLayoutParams(pamras);

            if (STYLE_OPEN_RESIZE) {
                // 开启智能压缩（暂时性能不好）
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(model.uri)
                        .setResizeOptions(new ResizeOptions(w, pamras.height))
                        .build();
                AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(v.getController())
                        .setImageRequest(request)
                        .setAutoPlayAnimations(true)
                        .build();
                v.setController(controller);
            } else {
                AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(v.getController())
                        .setAutoPlayAnimations(true)
                        .setUri(model.uri)
                        .build();
                v.setController(controller);
            }
        }
    }
}
