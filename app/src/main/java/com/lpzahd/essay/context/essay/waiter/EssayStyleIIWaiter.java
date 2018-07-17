package com.lpzahd.essay.context.essay.waiter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.lpzahd.Lists;
import com.lpzahd.Objects;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemChildTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.data.DataFactory;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay.EssayStyleIIAddDialog;
import com.lpzahd.essay.context.essay_.waiter.EssayAddComponent;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.essay.exotic.realm.Realmer;
import com.lpzahd.essay.tool.DateTime;
import com.lpzahd.waiter.consumer.State;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayStyleIIWaiter extends ToneActivityWaiter<EssayActivity> implements DataFactory.DataProcess<Essay, EssayStyleIIWaiter.EssayModel>, EssayStyleIIAddDialog.InputCallback, Receiver<Boolean> {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    @BindView(R.id.bottom_sheet_layout)
    BottomSheetLayout bottomSheetLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Realm mRealm;

    private DataFactory<Essay, EssayStyleIIWaiter.EssayModel> mFactoty;

    private EssayAdapter mAdapter;

    private SwipeRefreshWaiter mRefreshWaiter;

    private EssayAddComponent essayAddComponent;
    private boolean isShowComponent = false;

    public EssayStyleIIWaiter(EssayActivity essayActivity) {
        super(essayActivity);
    }

    @Override
    protected void init() {
        mFactoty = DataFactory.of(this);
        mRealm = Realm.getDefaultInstance();
        RxBus.get().registIfAbsent(EssayActivity.TAG, this);
    }


    @Override
    protected void destroy() {
        Realmer.close(mRealm);
        RxTaxi.get().unregist(PreviewPicWaiter.TAG);
        RxBus.get().unregist(EssayActivity.TAG);
    }

    @Override
    protected void initView() {

        essayAddComponent = new EssayAddComponent(context);
        essayAddComponent.init();

        bottomSheetLayout.addOnSheetStateChangeListener(state -> {
            setFabImageResource(state);

            if (state != BottomSheetLayout.State.HIDDEN)
                showSaveFabWithAniIfHide(fab);

            isShowComponent = state != BottomSheetLayout.State.HIDDEN;
        });


        setUpRecyclerView();
    }

    /**
     * 根据状态设置fab的图片资源
     */
    private void setFabImageResource(BottomSheetLayout.State state) {
        if (state == BottomSheetLayout.State.HIDDEN) {
            fab.setImageResource(R.drawable.ic_add_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_save_white_24dp);
        }
    }

    /**
     * 用动画显示保存样式的fab（如果它此时隐藏中）
     */
    private void showSaveFabWithAniIfHide(FloatingActionButton fab) {
        if (fab.getVisibility() == View.VISIBLE) return;

        fab.setVisibility(View.VISIBLE);

        ViewCompat.animate(fab)
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start();
    }

    /**
     * 设置recyclerview
     */
    private void setUpRecyclerView() {
        setRecyclverViewStyle();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new EssayAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(getEssayItemTouchListener());

        addWindowWaiter(mRefreshWaiter = new SwipeRefreshWaiter(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<? extends List> doRefresh(int page) {
                return mRealm.where(Essay.class)
                        .sort("date", Sort.DESCENDING)
                        .findAllAsync()
                        .asFlowable()
                        .filter(RealmResults::isLoaded)
                        .map((Function<RealmResults<Essay>, List>) mFactoty::processArray);
            }

        });

        mRefreshWaiter.setCount(Integer.MAX_VALUE);
        mRefreshWaiter.autoRefresh();
    }

    @NonNull
    private OnItemChildTouchListener<EssayHolder> getEssayItemTouchListener() {
        return new OnItemChildTouchListener<EssayHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, EssayHolder essayHolder, View child) {
                if (child == essayHolder.simpleDraweeView) {
                    go2PreviewAcitivity(essayHolder);
                } else if (child == essayHolder.moreIv) {
                    showUpdateSheetView(mAdapter.getItem(essayHolder.getAdapterPosition()).id);
                }
            }

            private void go2PreviewAcitivity(EssayHolder essayHolder) {
                PreviewPicActivity.startActivity(context);
                RxTaxi.get().regist(PreviewPicWaiter.TAG,
                        transmit(mAdapter.getItem(essayHolder.getAdapterPosition()).id));
            }

            private DataFactory<Image, PreviewPicWaiter.PreviewBean> getDataFactory() {
                return DataFactory.of(image -> {
                    PreviewPicWaiter.PreviewBean pic = new PreviewPicWaiter.PreviewBean();
                    pic.uri = Frescoer.uri(image.getPath(), ImageSource.SOURCE_FILE);
                    return pic;
                });
            }

            private Transmitter<List<PreviewPicWaiter.PreviewBean>> transmit(String id) {
                return () -> mRealm.where(Essay.class)
                        .equalTo("id", id)
                        .findFirstAsync()
                        .<Essay>asFlowable()
                        .filter(essay -> essay.isLoaded())
                        .filter(essay -> !Lists.empty(essay.getDefaultImages()))
                        .map(Essay::getDefaultImages)
                        .map(images -> getDataFactory().processArray(images));
            }

            @Override
            public void onLongClick(RecyclerView rv, EssayHolder essayHolder, View child) {
                final int position = essayHolder.getAdapterPosition();
                EssayModel model = mAdapter.getItem(position);
                String content = "你确定要删除 \'" + (!Strings.empty(model.title) ? model.title : model.content) + "\' 嘛？";
                showTipDialog(position, content);
            }

            private void showTipDialog(int position, String content) {
                new MaterialDialog.Builder(context)
                        .title("⚠️警告")
                        .content(content)
                        .positiveText(R.string.tip_positive)
                        .negativeText(R.string.tip_negative)
                        .onPositive((dialog, which) -> deleteItem(position))
                        .show();
            }
        };
    }


    @SuppressLint("CheckResult")
    private void deleteItem(int position) {
        try (Realm realm = Realm.getDefaultInstance()) {
            EssayModel model = mAdapter.getItem(position);
            realm.where(Essay.class)
                    .equalTo("id", model.id)
                    .findFirstAsync()
                    .<Essay>asFlowable()
                    .filter(essay -> essay.isLoaded())
                    .subscribe(essay -> {
                        realm.executeTransaction(realm1 -> essay.deleteFromRealm());
                        mAdapter.remove(position);
                        T.t("删除成功");
                    });
        }
    }

    private void showUpdateSheetView(String modelId) {
        View layout = essayAddComponent.inflate(bottomSheetLayout);
        essayAddComponent.update(modelId);
        bottomSheetLayout.showWithSheetView(layout);
    }

    private void setRecyclverViewStyle() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        recyclerView.setPadding(16, 0, 16, 0);
    }

    @OnClick(R.id.fab)
    void showEssayAddDialog() {
        if (!isShowComponent) {
            EssayStyleIIAddDialog dialog = new EssayStyleIIAddDialog();
            dialog.setInputCallback(this);
            dialog.show(context);
        } else {
            if (essayAddComponent.update())
                bottomSheetLayout.dismissSheet();
        }

    }


    @Override
    protected int optionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fix) {
            context.rxAction(fixGalleryImagesFlowable(), num -> {
                T.t("一共修复了%s张图片", num);
                mRefreshWaiter.autoRefresh();
            });
            return State.STATE_PREVENT;
        }
        return super.optionsItemSelected(item);
    }

    /**
     * 修复之前在相册中读取的照片丢失问题
     * 改为从'收藏'中获取
     */
    private Flowable<Integer> fixGalleryImagesFlowable() {
        return mRealm.where(Essay.class)
                .findAllAsync()
                .asFlowable()
                .filter(RealmResults::isLoaded)
                .map(this::filterImages)
                .map(this::extractImages)
                .map(this::motifyImagePath);
    }

    /**
     * 修改图片路径
     */
    private int motifyImagePath(List<Image> images) {
        int num = 0;
        try (Realm realm = Realm.getDefaultInstance()) {
            for (Image image : images) {
                Collection result = realm.where(Collection.class)
                        .equalTo("originalPath", image.getPath())
                        .findFirst();
                if(result != null) {
                    mRealm.beginTransaction();
                    image.setPath(result.getImage().getPath());
                    mRealm.commitTransaction();
                    num++;
                }
            }
        }
        return num;
    }

    @NonNull
    private List<Image> extractImages(List<Essay> essays) {
        List<Image> list = new ArrayList<>();
        for (Essay essay : essays) {
            for (Image image : essay.geteFile().getImages()) {
                if (!new File(image.getPath()).exists())
                    list.add(image);
            }
        }
        return list;
    }

    @NonNull
    private List<Essay> filterImages(RealmResults<Essay> essays) {
        List<Essay> list = new ArrayList<>(essays);
        Lists.removeIf(list, var1 -> Objects.isNull(var1.geteFile()) || Lists.empty(var1.geteFile().getImages()));
        return list;
    }

    @Override
    public EssayStyleIIWaiter.EssayModel process(Essay essay) {
        EssayStyleIIWaiter.EssayModel model = new EssayStyleIIWaiter.EssayModel();
        model.id = essay.getId();
        model.title = essay.getTitle();
        model.content = essay.getContent();
        model.date = DateTime.format(essay.getDate(), "yyyy-MM-dd");
        model.gravity = parseContent(model.content);

        if (!Objects.isNull(essay.geteFile()) && !Lists.empty(essay.geteFile().getImages())) {
            Image image = essay.geteFile().getImages().get(0);
            model.uri = Frescoer.uri(Objects.requireNonNull(image).getPath(), ImageSource.SOURCE_FILE);
        }
        return model;
    }

    private
    @EssayStyleIIWaiter.GRAVITY
    int parseContent(String content) {
        if (Strings.empty(content)) return GRAVITY_LEFT;

        if (content.length() < 10) return GRAVITY_CENTER;

        String[] lines = content.split("\n");
        boolean isPoet = true;
        int len = lines[0].length();
        for (int i = 1; i < lines.length; i++) {
            if (len != lines[i].length()) {
                isPoet = false;
                break;
            }
        }
        return isPoet ? GRAVITY_CENTER : GRAVITY_LEFT;
    }

    @Override
    public void onInput(EssayStyleIIAddDialog dialog, CharSequence title, CharSequence content) {
        final Essay essay = new Essay();
        essay.setTitle(title.toString());
        essay.setContent(content.toString());
        mRealm.executeTransactionAsync(realm -> realm.copyToRealm(essay), () -> {
            recyclerView.scrollToPosition(0);
            mAdapter.addFirst(mFactoty.process(essay));
        });
    }

    @Override
    public void receive(Flowable<Boolean> flowable) {
        context.addDispose(flowable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) mRefreshWaiter.autoRefresh();
                }));
    }


    static class EssayModel {
        String id;
        String title;
        String content;
        String date;
        @GRAVITY
        int gravity;

        Uri uri;
    }

    private static final int GRAVITY_LEFT = 1;
    private static final int GRAVITY_CENTER = 2;

    @IntDef({GRAVITY_LEFT, GRAVITY_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    private @interface GRAVITY {
    }

    static class EssayHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.top_layout)
        RelativeLayout topLayout;

        @BindView(R.id.bottom_layout)
        RelativeLayout bottomLayout;

        @BindView(R.id.simple_drawee_view)
        SimpleDraweeView simpleDraweeView;

        @BindView(R.id.date_tv)
        AppCompatTextView dateTv;

        @BindView(R.id.title_tv)
        AppCompatTextView titleTv;

        @BindView(R.id.content_tv)
        AppCompatTextView contentTv;

        @BindView(R.id.more_iv)
        AppCompatImageView moreIv;

        EssayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            GradientDrawable topDrawable = new GradientDrawable();
            topDrawable.setCornerRadii(new float[]{
                    16f, 16f, 16f, 16f, 0f, 0f, 0f, 0f
            });
            topDrawable.setColor(Color.parseColor("#e84887"));
            topLayout.setBackground(topDrawable);

            GradientDrawable bottomDrawable = new GradientDrawable();
            bottomDrawable.setCornerRadii(new float[]{
                    0f, 0f, 0f, 0f, 16f, 16f, 16f, 16f
            });
            bottomDrawable.setColor(Color.parseColor("#f7f7f7"));
            bottomLayout.setBackground(bottomDrawable);
        }
    }

    private class EssayAdapter extends ToneAdapter<EssayModel, EssayHolder> {

        EssayAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public EssayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EssayHolder(inflateItemView(R.layout.item_essay_style_02, parent));
        }

        @Override
        public void onBindViewHolder(@NonNull EssayHolder holder, int position) {
            EssayModel model = getItem(position);
            holder.dateTv.setText(model.date);
            if (Strings.empty(model.title)) {
                holder.titleTv.setVisibility(View.GONE);
            } else {
                holder.titleTv.setVisibility(View.VISIBLE);
                holder.titleTv.setText(model.title);
            }

            holder.contentTv.setText(model.content);
            if (model.gravity == GRAVITY_LEFT) {
                holder.contentTv.setGravity(Gravity.START);
            } else {
                holder.contentTv.setGravity(Gravity.CENTER);
            }

            if (Objects.isNull(model.uri)) {
                holder.simpleDraweeView.setVisibility(View.GONE);
            } else {
                holder.simpleDraweeView.setVisibility(View.VISIBLE);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(model.uri)
                        .setResizeOptions(new ResizeOptions(200, 200))
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.simpleDraweeView.getController())
                        .setImageRequest(request)
                        .setAutoPlayAnimations(true)
                        .build();
                holder.simpleDraweeView.setController(controller);
            }
        }
    }
}
