package com.lpzahd.gallery.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.Image;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.data.DataFactory;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.gallery.R;
import com.lpzahd.gallery.R2;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.presenter.multi.BucketPresenter;
import com.lpzahd.gallery.tool.MediaTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class MultiSelectPresenter extends ToneActivityWaiter<GalleryActivity> implements BucketPresenter.OnBucketClickListener, DataFactory.DataProcess<MediaTool.MediaBean,MultiSelectPresenter.MultiBean> {

    private static int MODE_SINGLE = 0;
    private static int MODE_MULTI = 1;

//    private final int LIMIT = 30;

    @BindView(R2.id.app_bar_layout)
    public AppBarLayout appBarLayout;

    @BindView(R2.id.left_tv)
    public AppCompatTextView leftTv;

    @BindView(R2.id.center_tv)
    public AppCompatTextView centerTv;

    @BindView(R2.id.right_tv)
    public AppCompatTextView rightTv;

    @BindView(R2.id.tool_bar)
    public Toolbar toolBar;

    @BindView(R2.id.swipe_refresh_layout)
    public SwipeRefreshLayout swipeRefershLayout;

    @BindView(R2.id.recycler_view)
    public RecyclerView recyclerView;

    @BindView(R2.id.folder_name_tv)
    public AppCompatTextView folderNameTv;

    @BindView(R2.id.preview_tv)
    public AppCompatTextView previewTv;

    @BindView(R2.id.boottom_bar_layout)
    public RelativeLayout boottomBarLayout;

    @BindView(R2.id.root_view_layout)
    public RelativeLayout rootViewLayout;

    private int mode = MODE_SINGLE;

    private int maxSize = 1;
    private int selectSize = 0;
    private String bucketId = MediaTool.MEDIA_NO_BUCKET;

    private List<MediaTool.MediaBean> mOriginalSource;
    private Map<String, BucketPresenter.BucketBean> mBucketMap;

    private MultiAdapter mAdapter;

    private DataFactory<MediaTool.MediaBean, MultiBean> mDataFactory;
    private SwipeRefreshWaiter mRefreshWaiter;

    private List<MediaTool.MediaBean> mSelected;
    private BucketPresenter mBucketPresenter;

    public MultiSelectPresenter(GalleryActivity activity, int maxSize) {
        super(activity);

        if (maxSize < 0) {
            throw new IllegalArgumentException("二逼不解释！");
        }

        this.maxSize = maxSize;
        mSelected = new ArrayList<>(maxSize);

        if (maxSize == 1)
            mode = MODE_SINGLE;
        else
            mode = MODE_MULTI;

    }

    @Override
    protected void init() {
        super.init();
        addWaiter(mBucketPresenter = new BucketPresenter(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            context.getWindow().setExitTransition(new Explode());
        }

        mDataFactory = DataFactory.of(this);
    }

    @Override
    protected void setContentView() {
        context.setContentView(R.layout.activity_multi_select);
    }

    @Override
    protected void initView() {
        toolBar.setTitle("图片");
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        rightTv.setVisibility(View.VISIBLE);
        Ui.setBackground(rightTv, createDefaultRightButtonBgDrawable());

        RxView.clicks(rightTv)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                    }
                });

        changeRightTxt();

        RxView.clicks(folderNameTv)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mBucketPresenter.showDialog(folderNameTv.getText().toString(), getBuckets(), MultiSelectPresenter.this);
                    }
                });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        mAdapter = new MultiAdapter(context, getScreenSize(context).widthPixels / 3);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<MultiHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, MultiHolder multiHolder) {
//                PreviewWaiter.startActivity(context, multiHolder.getAdapterPosition(),
//                        convertToPreview(mAdapter.getData()));
                PreviewWaiter.startActivity(context, multiHolder.getAdapterPosition(), bucketId);
            }

            private ArrayList<PreviewWaiter.PreviewBean> convertToPreview(List<MultiBean> source) {
                ArrayList<PreviewWaiter.PreviewBean> result = new ArrayList<>();
                if (Lists.empty(source)) return result;

                for (int i = 0, size = source.size(); i < size; i++) {
                    MultiBean media = source.get(i);
                    PreviewWaiter.PreviewBean preview = new PreviewWaiter.PreviewBean();
                    preview.uri = media.uri;
                    result.add(preview);
                }
                return result;
            }
        });
    }

    private ArrayList<BucketPresenter.BucketBean> getBuckets() {
        if (mOriginalSource == null) return new ArrayList<>(0);
        return new ArrayList<>(mBucketMap.values());
    }

    private Map<String, BucketPresenter.BucketBean> pick(List<MediaTool.MediaBean> source) {
        Map<String, BucketPresenter.BucketBean> map = new HashMap<>();
        if (Lists.empty(source)) return map;

        for (int i = 0, size = source.size(); i < size; i++) {
            MediaTool.MediaBean bean = source.get(i);
            BucketPresenter.BucketBean bucket = map.get(bean.getBucketId());
            if (bucket == null) {
                bucket = new BucketPresenter.BucketBean();
                bucket.setId(bean.getBucketId());
                bucket.setName(bean.getBucketDisplayName());
                bucket.setNum(1);
                bucket.setUri(Frescoer.uri(bean.getOriginalPath(), Image.SOURCE_FILE));
                map.put(bucket.getId(), bucket);
            } else {
                bucket.setNum(bucket.getNum() + 1);
            }
        }
        return map;
    }

    private void changeRightTxt() {
        rightTv.setText(String.format(Locale.getDefault(), "选择(%d/%d)", selectSize, maxSize));
    }

    private StateListDrawable createDefaultRightButtonBgDrawable() {
        int dp12 = Ui.dip2px(context, 12);
        int dp8 = Ui.dip2px(context, 8);
        float dp4 = Ui.dip2px(context, 4);
        float[] round = new float[]{dp4, dp4, dp4, dp4, dp4, dp4, dp4, dp4};
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        pressedDrawable.setPadding(dp12, dp8, dp12, dp8);
        pressedDrawable.getPaint().setColor(Color.parseColor("#2589C8"));

        ShapeDrawable normalDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        normalDrawable.setPadding(dp12, dp8, dp12, dp8);
        normalDrawable.getPaint().setColor(Color.parseColor("#25C8C6"));

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
    }

    @Override
    protected void initData() {
        addWindowWaiter(mRefreshWaiter = new SwipeRefreshWaiter(swipeRefershLayout, recyclerView) {

            @Override
            public Flowable<? extends List> doRefresh(final int page) {
                return Flowable.create(new FlowableOnSubscribe<List<MediaTool.MediaBean>>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<List<MediaTool.MediaBean>> e) throws Exception {
                        List<MediaTool.MediaBean> mediaBeanList = MediaTool.getImageFromContext(context, bucketId);
                        e.onNext(mediaBeanList);
                    }
                }, BackpressureStrategy.BUFFER)
                        .filter(new Predicate<List<MediaTool.MediaBean>>() {
                            @Override
                            public boolean test(@NonNull List<MediaTool.MediaBean> mediaBeen) throws Exception {
                                if(Lists.empty(mOriginalSource)) {
                                    mOriginalSource = mediaBeen;
                                    mBucketMap = pick(mediaBeen);
                                } else {
                                    if(!Lists.empty(mediaBeen) && mediaBeen.size() > mOriginalSource.size()) {
                                        mOriginalSource = mediaBeen;
                                        mBucketMap = pick(mediaBeen);
                                    }
                                }
                                return !Lists.empty(mediaBeen);
                            }
                        })
                        .map(new Function<List<MediaTool.MediaBean>, List<MultiBean>>() {
                            @Override
                            public List<MultiBean> apply(@NonNull List<MediaTool.MediaBean> mediaBeen) throws Exception {
                                return mDataFactory.processArray(mediaBeen);
                            }
                        })
                        .subscribeOn(Schedulers.io());
            }

        });

        mRefreshWaiter.setCount(Integer.MAX_VALUE);
        mRefreshWaiter.autoRefresh();

    }

    private static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    @Override
    public void click(BucketPresenter.BucketDialog dialog, int position, BucketPresenter.BucketBean bucket) {
        toolBar.setTitle(bucket.getName());

        bucketId = bucket.getId();
        List<MediaTool.MediaBean> adapterData = new ArrayList<>();
        for (int i = 0, size = mOriginalSource.size(); i < size; i++) {
            MediaTool.MediaBean bean = mOriginalSource.get(i);
            if(Strings.equals(bucketId, bean.getBucketId())) {
                adapterData.add(bean);
            }
        }
        mAdapter.setData(mDataFactory.processArray(adapterData));
        recyclerView.scrollToPosition(0);

        dialog.dismiss();
    }

    @Override
    public MultiBean process(MediaTool.MediaBean mediaBean) {
        MultiBean bean = new MultiBean();
        bean.uri = Frescoer.uri(mediaBean.getOriginalPath(), Image.SOURCE_FILE);
        return bean;
    }

    public static class MultiBean {
        public Uri uri;
        public boolean checked;
    }

    private class MultiAdapter extends ToneAdapter<MultiBean, MultiHolder> {

        private int size = 200;
        private List<Integer> slects;

        private MultiAdapter(Context context, int size) {
            super(context);
            this.size = size;
            slects = new ArrayList<>();
        }

        public List<Integer> getSlects() {
            return slects;
        }

        public void setSlects(List<Integer> slects) {
            this.slects = slects;
        }

        @Override
        public MultiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final MultiHolder holder = new MultiHolder(inflateItemView(R.layout.item_media_select_grid, parent));
            holder.setCheckBoxClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    MultiBean bean = getItem(position);
                    if (bean.checked) {
                        bean.checked = false;
                        slects.remove(Integer.valueOf(position));
                        selectSize = slects.size();
                        changeRightTxt();
                    } else {
                        if (slects.size() >= maxSize) {
                            T.t("你最多只能选择" + maxSize + "张照片");
                            holder.checkBox.setChecked(false);
                        } else {
                            bean.checked = true;
                            slects.add(position);
                            selectSize = slects.size();
                            changeRightTxt();
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MultiHolder holder, int position) {
            MultiBean bean = getItem(position);
            holder.checkBox.setChecked(bean.checked);
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

    static class MultiHolder extends ToneAdapter.ToneHolder {

        @BindView(R2.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        @BindView(R2.id.check_box)
        AppCompatCheckBox checkBox;

        MultiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setCheckBoxClickListener(View.OnClickListener listener) {
            if (listener != null)
                checkBox.setOnClickListener(listener);
        }

    }
}
