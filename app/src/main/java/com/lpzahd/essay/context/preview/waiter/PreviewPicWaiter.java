package com.lpzahd.essay.context.preview.waiter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.Lists;
import com.lpzahd.atool.keeper.Downloads;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.keeper.storage.CallBack;
import com.lpzahd.atool.keeper.storage.Result;
import com.lpzahd.atool.keeper.storage.Task;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.waiter.FileDownloadWaiter;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.tool.OkHttpRxAdapter;
import com.lpzahd.gallery.context.PreviewActivity;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class PreviewPicWaiter extends ToneActivityWaiter<PreviewPicActivity> {

    public static final String TAG = "com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter";

    public static final String TAG_INDEX = "com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter_index";

    private static final String SHARE_ELEMENT_NAME = "share_pic";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.h_text_view)
    HTextView hTextView;

    @BindView(R.id.batch_iv)
    AppCompatImageView batchIv;

    private Transmitter<List<PreviewBean>> mTransmitter;

    private PreviewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private FileDownloadWaiter mFileDownloadWaiter;

    /**
     * fresco 似乎不大好用共享元素动画， 说是用这个ChangeBounds
     */
    @Deprecated
    public static void startActivity(Activity activity, View share) {
        Intent intent = new Intent(activity, PreviewPicActivity.class);
        ViewCompat.setTransitionName(share, SHARE_ELEMENT_NAME);
        ActivityOptionsCompat opt = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                share, SHARE_ELEMENT_NAME);
        ActivityCompat.startActivity(activity, intent, opt.toBundle());
//        ActivityCompat.setExitSharedElementCallback(activity, new SharedElementCallback() {
//            @Override
//            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                super.onMapSharedElements(names, sharedElements);
//            }
//        });
//        ActivityOptionsCompat opt = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
//                new Pair<>(v1, ViewCompat.getTransitionName(v1)),
//                new Pair<>(v2, ViewCompat.getTransitionName(v2)));
//        ActivityCompat.startActivity(activity, intent, opt.toBundle());
//        shareView.setDrawingCacheEnabled(false);
    }

    public PreviewPicWaiter(PreviewPicActivity previewPicActivity) {
        super(previewPicActivity);
    }


    @Override
    protected void init() {
        super.init();
        addWaiter(mFileDownloadWaiter = new FileDownloadWaiter(context));
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        return super.checkArgus(intent) && ((mTransmitter = RxTaxi.get().pull(TAG)) != null);
    }

    @Override
    protected void initView() {
        hTextView.setAnimateType(HTextViewType.SCALE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<PreviewHolder>(recyclerView) {
            @Override
            public void onLongClick(RecyclerView rv, PreviewHolder previewHolder) {
                mFileDownloadWaiter.showDownLoadDialog(
                        mAdapter.getItem(previewHolder.getAdapterPosition()).uri.toString());
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            Disposable dispose;
            ObservableEmitter<PreviewHolder> emitter;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = snapHelper.findSnapView(mLayoutManager);
                    if (snapView != null) {

                        final PreviewHolder holder = (PreviewHolder) recyclerView.getChildViewHolder(snapView);
                        if(emitter == null) {
                            dispose = Observable.create(new ObservableOnSubscribe<PreviewHolder>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<PreviewHolder> e) throws Exception {
                                    emitter = e;
                                    e.onNext(holder);
                                }
                            })
                            .throttleLast(50, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<PreviewHolder>() {
                                        @Override
                                        public void accept(PreviewHolder previewHolder) throws Exception {
                                            if(hTextView != null)
                                                hTextView.animateText("第" + previewHolder.getAdapterPosition() + "张");
                                        }
                                    });
                            context.addDispose(dispose);
                        } else {
                            emitter.onNext(holder);
                        }
                    }
                }
            }
        });

        mAdapter = new PreviewAdapter(context);
        recyclerView.setAdapter(mAdapter);

        mTransmitter.transmit()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<PreviewBean>, Publisher<Integer>>() {
                    @Override
                    public Publisher<Integer> apply(List<PreviewBean> previewBeans) throws Exception {
                        mAdapter.setData(previewBeans);
                        showBatchIv(previewBeans.size());

                        Flowable<Integer> indexObservable = RxTaxi.get().<Integer>pull(TAG_INDEX).transmit();
                        return indexObservable != null ? indexObservable : Flowable.just(0);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>()  {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        recyclerView.scrollToPosition(integer);
                        if(!Lists.empty(mAdapter.getData())) {
                            hTextView.animateText("第" + integer +"张");
                        }
                    }
                });

    }

    private void showBatchIv(int size) {
        if(size < 1) {
            batchIv.setVisibility(View.GONE);
            return ;
        }

        batchIv.setVisibility(View.VISIBLE);
        RxView.clicks(batchIv)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(mAdapter == null || Lists.empty(mAdapter.getData())) {
                            T.t("暂无图片哦！");
                        } else {
                            List<PreviewBean> pics = mAdapter.getData();

                            String[] urls = new String[pics.size()];

                            for(int i = 0, size = pics.size(); i < size; i++) {
                                urls[i] = pics.get(i).uri.toString();
                            }
                            mFileDownloadWaiter.showDownLoadDialog(urls);
                        }
                    }
                });

        if(size > 9) {
            batchIv.setImageResource(R.drawable.ic_filter_9_plus_white_24dp);
            return ;
        }

        switch (size) {
            case 1:
                batchIv.setImageResource(R.drawable.ic_filter_1_white_24dp);
                break;
            case 2:
                batchIv.setImageResource(R.drawable.ic_filter_2_white_24dp);
                break;
            case 3:
                batchIv.setImageResource(R.drawable.ic_filter_3_white_24dp);
                break;
            case 4:
                batchIv.setImageResource(R.drawable.ic_filter_4_white_24dp);
                break;
            case 5:
                batchIv.setImageResource(R.drawable.ic_filter_5_white_24dp);
                break;
            case 6:
                batchIv.setImageResource(R.drawable.ic_filter_6_white_24dp);
                break;
            case 7:
                batchIv.setImageResource(R.drawable.ic_filter_7_white_24dp);
                break;
            case 8:
                batchIv.setImageResource(R.drawable.ic_filter_8_white_24dp);
                break;
            case 9:
                break;
        }

    }

    public static class PreviewBean {
        public Uri uri;
    }

    public static class PreviewHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.photo_drawee_view)
        PhotoDraweeView photoDraweeView;

        public PreviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PreviewAdapter extends ToneAdapter<PreviewBean, PreviewHolder> {

        public PreviewAdapter(Context context) {
            super(context);
        }

        @Override
        public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PreviewHolder(inflateItemView(R.layout.item_preview_pic, parent));
        }

        @Override
        public void onBindViewHolder(PreviewHolder holder, int position) {
            PreviewBean bean = getItem(position);
            showPhoto(holder.photoDraweeView, bean);
        }

        private void showPhoto(final PhotoDraweeView photoDraweeView, PreviewBean bean) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setUri(bean.uri)
                    .setOldController(photoDraweeView.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            if (imageInfo == null) {
                                return;
                            }
                            photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    })
                    .build();
            photoDraweeView.setController(controller);
        }
    }
}
