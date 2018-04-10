package com.lpzahd.essay.common.waiter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.atool.action.Check;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.tone.adapter.OnItemChildTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.TranstionPicActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;

public class MediaSelectWaiter extends ToneActivityWaiter<AppCompatActivity> {

    public static final String TAG = "com.lpzahd.essay.common.waiter.MediaSelectWaiter";

    private static int MODE_SINGLE = 0;
    private static int MODE_MULTI = 1;

    @BindView(R.id.app_bar_layout)
    public AppBarLayout appBarLayout;

    @BindView(R.id.left_tv)
    public AppCompatTextView leftTv;

    @BindView(R.id.center_tv)
    public AppCompatTextView centerTv;

    @BindView(R.id.right_tv)
    public AppCompatTextView rightTv;

    @BindView(R.id.tool_bar)
    public Toolbar toolBar;

    @BindView(R.id.swipe_refresh_layout)
    public SwipeRefreshLayout swipeRefershLayout;

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.folder_name_tv)
    public AppCompatTextView folderNameTv;

    @BindView(R.id.preview_tv)
    public AppCompatTextView previewTv;

    @BindView(R.id.boottom_bar_layout)
    public RelativeLayout boottomBarLayout;

    @BindView(R.id.root_view_layout)
    public RelativeLayout rootViewLayout;

    private int mode = MODE_SINGLE;

    private int maxSize = 1;
    private int selectSize = 0;

    private Queue<Integer> slects;

    private MediaAdapter mAdapter;

    private SwipeRefreshWaiter mRefreshWaiter;

    private SwipeRefreshWaiter.DataFlowable mDataFlowable;

    public MediaSelectWaiter(int maxSize, SwipeRefreshWaiter.DataFlowable flowable) {
        super();

        if (maxSize < 0) {
            throw new IllegalArgumentException("二逼不解释！");
        }

        this.maxSize = maxSize;

        if (maxSize == 1)
            mode = MODE_SINGLE;
        else
            mode = MODE_MULTI;

        this.mDataFlowable = flowable;
    }

    @Override
    protected void init() {
        super.init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            context.getWindow().setExitTransition(new Explode());
        }

        slects = new LinkedList<>();
    }

    @Override
    protected void setContentView() {
        context.setContentView(com.lpzahd.gallery.R.layout.activity_multi_select);
    }

    @Override
    protected void initView() {
        toolBar.setTitle("图片");
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        rightTv.setVisibility(View.VISIBLE);
        Ui.setBackground(rightTv, createDefaultRightButtonBgDrawable());

        new RxBus.BusService(TAG);

        RxView.clicks(rightTv)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(Check.Empty.check(slects, () -> T.t("没有选择一张图片"))) return;

                    final List<MediaBean> source = mAdapter.getData();
                    List<MediaBean> results = new ArrayList<>();
                    for (Integer select : slects) {
                        results.add(source.get(select));
                    }
                    RxBus.get().post(TAG, results);
                    context.onBackPressed();
                });

        changeRightTxt();

        RxView.clicks(folderNameTv)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> T.t("....."));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        mAdapter = new MediaAdapter(context, getScreenSize(context).widthPixels / 3);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemChildTouchListener<MediaHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, MediaHolder holder, View child) {
                if (child == holder.checkBox) {
                    int position = holder.getAdapterPosition();
                    MediaBean bean = mAdapter.getItem(position);
                    if (bean.checked) {
                        bean.checked = false;
                        slects.remove(position);
                        holder.checkBox.setChecked(false);
                        selectSize = slects.size();
                        changeRightTxt();
                    } else {
                        if (slects.size() >= maxSize) {
                            T.t("你最多只能选择" + maxSize + "张照片");
                            holder.checkBox.setChecked(false);
                        } else {
                            bean.checked = true;
                            slects.add(position);
                            holder.checkBox.setChecked(true);
                            selectSize = slects.size();
                            changeRightTxt();
                        }
                    }
                } else if (child == holder.imageDraweeView) {

                    holder.imageDraweeView.setLegacyVisibilityHandlingEnabled(true);
                    holder.imageDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
                    holder.imageDraweeView.getHierarchy().setActualImageFocusPoint(new PointF(1, 0.5f));

                    final int position = holder.getAdapterPosition();

                    TranstionPicActivity.startActivity(context, holder.imageDraweeView);

                    RxTaxi.get().regist(TranstionPicActivity.TAG, () -> Flowable.just(mAdapter.getItem(position).uri));

                }
            }
        });
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
            public Flowable<? extends List> doRefresh(int page) {
                mRefreshWaiter.setCount(mDataFlowable.getPage());
                return mDataFlowable.doRefresh(page);
            }
        });

        mRefreshWaiter.autoRefresh();

    }

    private static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if(wm == null) return null;

        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    public static class MediaBean {
        public Uri uri;
        public boolean checked;
    }

    private class MediaAdapter extends ToneAdapter<MediaBean, MediaHolder> {

        private int size = 200;

        private MediaAdapter(Context context, int size) {
            super(context);
            this.size = size;
        }

        @Override
        public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MediaHolder(inflateItemView(com.lpzahd.gallery.R.layout.item_media_select_grid, parent));
        }

        @Override
        public void onBindViewHolder(MediaHolder holder, int position) {
            MediaBean bean = getItem(position);
            holder.checkBox.setChecked(bean.checked);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                    .setResizeOptions(ResizeOptions.forSquareSize(size))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            holder.imageDraweeView.setController(controller);
        }
    }

    static class MediaHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        @BindView(R.id.check_box)
        AppCompatCheckBox checkBox;

        MediaHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            checkBox.setClickable(false);
        }
    }
}