package com.lpzahd.essay.context.essay.waiter;

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

import com.afollestad.materialdialogs.DialogAction;
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
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.essay.EFile;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmList;
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
        super.init();
        mFactoty = DataFactory.of(this);
        mRealm = Realm.getDefaultInstance();
        RxBus.get().registIfAbsent(EssayActivity.TAG, this);
    }

    @Override
    protected void initView() {
        super.initView();

        essayAddComponent = new EssayAddComponent(context);
        essayAddComponent.init();

        bottomSheetLayout.addOnSheetStateChangeListener(state -> {
            if(state == BottomSheetLayout.State.HIDDEN) {
                fab.setImageResource(R.drawable.ic_add_white_24dp);
                isShowComponent = false;
            } else {
                isShowComponent = true;
                fab.setImageResource(R.drawable.ic_save_white_24dp);

                if(fab.getVisibility() != View.VISIBLE) {
                    fab.setVisibility(View.VISIBLE);

                    ViewCompat.animate(fab).translationY(0)
                            .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                            .start();
                }

            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        recyclerView.setPadding(16, 0, 16, 0);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new EssayAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemChildTouchListener<EssayHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, EssayHolder essayHolder, View child) {
                if (child == essayHolder.simpleDraweeView) {
//                    PreviewPicActivity.startActivity(context);
                    PreviewPicWaiter.startActivity(context, child);
                    RxTaxi.get().regist(PreviewPicWaiter.TAG,
                            transmit(mAdapter.getItem(essayHolder.getAdapterPosition()).id));
                }

                else if(child == essayHolder.moreIv) {
                    View layout = essayAddComponent.inflate(bottomSheetLayout);
                    bottomSheetLayout.showWithSheetView(layout);

                    EssayModel model = mAdapter.getItem(essayHolder.getAdapterPosition());
                    essayAddComponent.update(model.id);
                }
            }

            private Transmitter<List<PreviewPicWaiter.PreviewBean>> transmit(final String id) {
                return () -> {
                    Essay target = mRealm.where(Essay.class)
                            .equalTo("id", id)
                            .findFirst();
                    if (Objects.isNull(target) || Lists.empty(target.getDefaultImages()))
                        return null;
                    return Flowable.just(target.getDefaultImages())
                            .map(images -> {
                                List<PreviewPicWaiter.PreviewBean> pics = new ArrayList<>();
                                for (int i = 0, size = images.size(); i < size; i++) {
                                    PreviewPicWaiter.PreviewBean pic = new PreviewPicWaiter.PreviewBean();
                                    pic.uri = Frescoer.uri(images.get(i).getPath(), ImageSource.SOURCE_FILE);
                                    pics.add(pic);
                                }
                                return pics;
                            });
                };

            }

            @Override
            public void onLongClick(RecyclerView rv, EssayHolder essayHolder, View child) {
                super.onLongClick(rv, essayHolder, child);

                final int position = essayHolder.getAdapterPosition();
                EssayModel model = mAdapter.getItem(position);

                final String id = model.id;
                String content = "你确定要删除 " + (!Strings.empty(model.title) ? model.title : model.content) + " 嘛？";
                new MaterialDialog.Builder(context)
                        .title("删除")
                        .content(content)
                        .positiveText(R.string.tip_positive)
                        .negativeText(R.string.tip_negative)
                        .onPositive((dialog, which) -> {
                            Realm realm = Realm.getDefaultInstance();
                            Essay essay = realm.where(Essay.class)
                                    .equalTo("id", id)
                                    .findFirst();

                            if(essay != null) {
                                realm.beginTransaction();
                                essay.deleteFromRealm();
                                realm.commitTransaction();
                                mAdapter.remove(position);
                                T.t("删除成功");
                            }

                            realm.close();

                        })
                        .show();
            }
        });

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

    @OnClick(R.id.fab)
    void showEssayAddDialog() {
        if(isShowComponent) {
            if(essayAddComponent.update()) bottomSheetLayout.dismissSheet();
        } else {
            EssayStyleIIAddDialog dialog = new EssayStyleIIAddDialog();
            dialog.setInputCallback(this);
            dialog.show(context);
        }

    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
        RxTaxi.get().unregist(PreviewPicWaiter.TAG);
        RxBus.get().unregist(EssayActivity.TAG);
    }

    @Override
    protected int optionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_fix) {
            fixGalleryImages();
            return State.STATE_PREVENT;
        }
        return super.optionsItemSelected(item);
    }

    private Disposable mFixDispose;

    /**
     * 修复之前在相册中读取的照片丢失问题
     * 改为从'收藏'中获取
     */
    private void fixGalleryImages() {
        mFixDispose = mRealm.where(Essay.class)
                .findAllAsync()
                .asFlowable()
                .filter(RealmResults::isLoaded)
                .map(essays -> {
                    List<Essay> filters = new ArrayList<>();
                    for (Essay essay : essays) {
                        EFile eFile = essay.geteFile();
                        if (Objects.isNull(eFile)) continue;
                        List<Image> images = eFile.getImages();
                        if (Lists.empty(images)) continue;

                        for (Image image : images) {
                            if (!new File(image.getPath()).exists()) {
                                filters.add(essay);
                                break;
                            }
                        }
                    }
                    return filters;
                })
                .map(essays -> {
                    if (Lists.empty(essays)) return 0;

                    Realm realm = Realm.getDefaultInstance();

                    int num = 0;
                    for (Essay essay : essays) {
                        List<Image> images = essay.getDefaultImages();
                        int i = 0;
                        for (Image image : images) {
                            if (!new File(image.getPath()).exists()) {
                                Collection result = realm.where(Collection.class)
                                        .equalTo("originalPath", image.getPath())
                                        .findFirst();
                                if (result != null) {
                                    i++;
                                    mRealm.beginTransaction();
                                    image.setPath(result.getImage().getPath());
                                    mRealm.commitTransaction();
                                }
                            }
                        }
                        num += i;
                    }
                    realm.close();
                    return num;
                })
                .subscribe(integer -> {
                    if (integer == 0) {
                        T.t("修复失败!");
                    } else {
                        T.t("一共修复了%s张图片", integer);
                        mRefreshWaiter.autoRefresh();
                    }
                   context.disposeSafely(mFixDispose);

                }, throwable -> {
                    T.t(throwable.toString());
                    context.disposeSafely(mFixDispose);
                });
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
            model.uri = Frescoer.uri(essay.geteFile().getImages().get(0).getPath(), ImageSource.SOURCE_FILE);
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
