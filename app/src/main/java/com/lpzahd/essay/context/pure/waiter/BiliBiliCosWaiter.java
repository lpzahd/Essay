package com.lpzahd.essay.context.pure.waiter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import com.lpzahd.essay.common.waiter.FileDownloadWaiter;
import com.lpzahd.essay.context.instinct.waiter.YiyiBoxMediaWaiter;
import com.lpzahd.essay.context.instinct.waiter.YiyiBoxWaiter;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.context.pure.BiliBiliCosActivity;
import com.lpzahd.essay.context.pure.bilibili.BiliBiliCos;
import com.lpzahd.essay.exotic.retrofit.Net;
import com.lpzahd.waiter.consumer.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者 : 迪
 * 时间 : 2017/11/17.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class BiliBiliCosWaiter extends ToneActivityWaiter<BiliBiliCosActivity> implements View.OnClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @BindView(R.id.tool_bar_layout)
    FrameLayout toolBarLayout;

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

    private FileDownloadWaiter mFileDownloadWaiter;

    private CosRefreshWaiter mRefreshWaiter;

    private PureAdapter mAdapter;

    private int QUERY_COUNT = 20;

    public BiliBiliCosWaiter(BiliBiliCosActivity activity) {
        super(activity);
    }

    @Override
    protected void init() {
        super.init();
        addWaiter(mFileDownloadWaiter = new FileDownloadWaiter(context));
    }

    @Override
    protected int createOptionsMenu(Menu menu) {
        context.getMenuInflater().inflate(R.menu.menu_bilibili, menu);
        return super.createOptionsMenu(menu);
    }

    @Override
    protected int optionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_hot) {
            toolBar.setTitle(R.string.cos_hot);
            mRefreshWaiter.setType(Net.BiliPhotosApi.HOT);
            mRefreshWaiter.autoRefresh();
            recyclerView.scrollToPosition(0);
            return State.STATE_TRUE;
        }

        if (id == R.id.action_new) {
            toolBar.setTitle(R.string.cos_new);
            mRefreshWaiter.setType(Net.BiliPhotosApi.NEW);
            mRefreshWaiter.autoRefresh();
            recyclerView.scrollToPosition(0);
            return State.STATE_TRUE;
        }

        return super.optionsItemSelected(item);
    }

    @Override
    protected void initView() {
        toolBar.setTitle("Cos");
        context.setSupportActionBar(toolBar);

        pageFab.setOnClickListener(this);
        randomPageFab.setOnClickListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new PureAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<PureHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, PureHolder holder) {
                PreviewPicActivity.startActivity(context);

                final int position = holder.getAdapterPosition();
                RxTaxi.get().regist(PreviewPicWaiter.TAG, new Transmitter() {
                    @Override
                    public Flowable transmit() {
                        List<Photo> photo = mAdapter.getItem(position);
                        List<PreviewPicWaiter.PreviewBean> pics = new ArrayList<>();
                        for(Photo p : photo) {
                            PreviewPicWaiter.PreviewBean bean = new PreviewPicWaiter.PreviewBean();
                            bean.uri = p.uri;
                            pics.add(bean);
                        }

                        return Flowable.just(pics);
                    }
                });
            }
        });

        mRefreshWaiter = new CosRefreshWaiter(swipeRefreshLayout, recyclerView);
        mRefreshWaiter.setSwipeRefreshCallBack(new SwipeRefreshWaiter.SimpleCallBack() {
            @Override
            public void onPtrComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                QUERY_COUNT = mRefreshWaiter.getCountPages();
                refreshPageFab(page + 1, page - start + 1);
            }

            @Override
            public void onLoadComplete(int start, int page, @RefreshProcessor.LoadState int loadState) {
                QUERY_COUNT = mRefreshWaiter.getCountPages();
                refreshPageFab(page + 1, page - start + 1);
            }
        });

        refreshPageFab(0, 0);
        mRefreshWaiter.autoRefresh();
    }

    private void refreshPageFab(int page, int total) {
        currentPageFab.setTitle(String.format("当前是第%s(%s)页", page, QUERY_COUNT));
        totalPageFab.setTitle("当前一共看了" + total + "页");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page_fab:
                searchSkipPage();
                break;
            case R.id.random_page_fab:
                searchRandomPage();
                break;
        }
    }

    private void searchSkipPage() {
        new MaterialDialog.Builder(context)
                .title("指定页码搜索")
                .inputRange(0, 5)
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

                            if (page > QUERY_COUNT) {
                                page = page % QUERY_COUNT;
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
        RxTaxi.get().unregist(YiyiBoxMediaWaiter.TAG);
    }

    private static class CosRefreshWaiter extends DspRefreshWaiter<BiliBiliCos.DataBean.ItemsBean, List<Photo>> {

        private int countPages = 10;

        public int getCountPages() {
            return countPages;
        }

        private String type = Net.BiliPhotosApi.HOT;

        CosRefreshWaiter(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView) {
            super(swipeRefreshLayout, recyclerView);
        }

        void setType(String type) {
            this.type = type;
        }

        @Override
        public Flowable<List<BiliBiliCos.DataBean.ItemsBean>> doRefresh(final int page) {
            return Net.get().searchCos(type, page, 20)
                    .map(new Function<BiliBiliCos, List<BiliBiliCos.DataBean.ItemsBean>>() {
                        @Override
                        public List<BiliBiliCos.DataBean.ItemsBean> apply(BiliBiliCos biliBiliCos) throws Exception {
                            if(biliBiliCos.getCode() != 0)
                                return Collections.emptyList();

                            int total = biliBiliCos.getData().getTotal_count();
                            if(total > 0)
                                countPages = total;
                            else
                                countPages = page;

                            return biliBiliCos.getData().getItems();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .toFlowable(BackpressureStrategy.BUFFER);
        }

        @Override
        public List<Photo> process(BiliBiliCos.DataBean.ItemsBean itemBean) {
            List<BiliBiliCos.DataBean.ItemsBean.ItemBean.PicturesBean> picList = itemBean.getItem().getPictures();
            List<Photo> photos = new ArrayList<>();

            for (BiliBiliCos.DataBean.ItemsBean.ItemBean.PicturesBean bean : picList) {
                Photo photo = new Photo();
                photo.uri = Frescoer.uri(bean.getImg_src(), ImageSource.SOURCE_NET);
                photos.add(photo);
            }
            return photos;
        }
    }

    private static class Photo {
        Uri uri;
    }

    class PureHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;

        private PicAdapter adapter;

        private Context context;

        PureHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            context = itemView.getContext();

            recyclerView.setHasFixedSize(true);
            LinearLayoutManager manager = new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);

            adapter = new PicAdapter(context);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<PicHolder>(recyclerView) {
                @Override
                public void onClick(RecyclerView rv, PicHolder holder) {

                    final int position = holder.getAdapterPosition();
                    RxTaxi.get().regist(PreviewPicWaiter.TAG_INDEX, new Transmitter() {
                        @Override
                        public Flowable transmit() {
                            return Flowable.just(position);
                        }
                    });
                }

                @Override
                public void onLongClick(RecyclerView rv, PicHolder holder) {
                    final int position = holder.getAdapterPosition();
                    mFileDownloadWaiter.showDownLoadDialog(adapter.getItem(position).uri.toString());
                }
            });
        }

        void setData(List<Photo> photos) {
            adapter.setData(photos);
        }
    }

    private class PureAdapter extends ToneAdapter<List<Photo>, PureHolder> {

        public PureAdapter(Context context) {
            super(context);
        }

        @Override
        public PureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PureHolder(inflateItemView(R.layout.item_pure_bx, parent));
        }

        @Override
        public void onBindViewHolder(PureHolder holder, int position) {
            holder.setData(getItem(position));
        }
    }

    public static class PicHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        PicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PicAdapter extends ToneAdapter<Photo, PicHolder> {

        private int size;

        public PicAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 120);
        }

        @Override
        public PicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PicHolder(inflateItemView(R.layout.item_pure_photo, parent));
        }

        @Override
        public void onBindViewHolder(PicHolder holder, int position) {
            Photo bean = getItem(position);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                    .setResizeOptions(new ResizeOptions(size, size))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            holder.imageDraweeView.setController(controller);
        }
    }
    
}
