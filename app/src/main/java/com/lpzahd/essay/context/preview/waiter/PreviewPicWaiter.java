package com.lpzahd.essay.context.preview.waiter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
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
import com.lpzahd.Lists;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.tool.OkHttpRxAdapter;

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

    private Transmitter<List<PreviewBean>> mTransmitter;

    private PreviewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


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
                showFileDownDialog(mAdapter.getItem(previewHolder.getAdapterPosition()).uri);
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

        RxTaxi.get().<Integer>pull(TAG_INDEX).transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
    }

    private void showFileDownDialog(final Uri picUri) {
        String uriStr = picUri.toString();
        final String picName = uriStr.substring(uriStr.lastIndexOf("/") + 1).trim();
        new MaterialDialog.Builder(context)
                .title("图片下载")
                .content(picName)
                .positiveText(R.string.tip_positive)
                .negativeText(R.string.tip_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(picUri.toString())
                                .addHeader("referer", picUri.getHost())
                                .build();
                        OkHttpRxAdapter.adapter(client.newCall(request))
                                .subscribeOn(Schedulers.io())
                                .filter(new Predicate<Response>() {
                                    @Override
                                    public boolean test(@NonNull Response response) throws Exception {
                                        return response.isSuccessful();
                                    }
                                })
                                .map(new Function<Response, ResponseBody>() {
                                    @Override
                                    public ResponseBody apply(@NonNull Response response) throws Exception {
                                        return response.body();
                                    }
                                })
                                .map(new Function<ResponseBody, Boolean>() {
                                    @Override
                                    public Boolean apply(@NonNull ResponseBody body) throws Exception {
                                        Files files = Keeper.getF();
                                        String filePath = files.getFilePath(Files.Scope.PHOTO_RAW, picName);
                                        return Files.streamToFile(body.byteStream(), filePath);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            T.t(picName + "图片下载完成");
                                        } else {
                                            T.t(picName + "图片下载失败");
                                        }
                                    }
                                });
                    }
                })
                .show();
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
