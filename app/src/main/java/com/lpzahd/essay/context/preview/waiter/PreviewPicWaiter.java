package com.lpzahd.essay.context.preview.waiter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.lpzahd.Lists;
import com.lpzahd.atool.ui.L;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.PreviewPicActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class PreviewPicWaiter extends ToneActivityWaiter<PreviewPicActivity> {

    public static final String TAG = "com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.h_text_view)
    HTextView hTextView;

    private Transmitter<List<PreviewBean>> mTransmitter;

    private PreviewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            Disposable animDispose;
            ObservableEmitter<PreviewHolder> emitter;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = snapHelper.findSnapView(mLayoutManager);
                    if (snapView != null) {

                        final PreviewHolder holder = (PreviewHolder) recyclerView.getChildViewHolder(snapView);
                        if(emitter == null) {
                            animDispose = Observable.create(new ObservableOnSubscribe<PreviewHolder>() {
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
                                            L.e("htextview : " + hTextView);
                                            L.e("holder : " + previewHolder);
                                            L.e("position : " + previewHolder.getAdapterPosition());
                                            hTextView.animateText("第" + previewHolder.getAdapterPosition() + "张");
                                        }
                                    });
                            context.addDispose(animDispose);
                        } else {
                            emitter.onNext(holder);
                        }
//                        if(observer == null) {
//                            Observable observer = Observable.create(new ObservableOnSubscribe<PreviewHolder>() {
//                                @Override
//                                public void subscribe(@NonNull ObservableEmitter<PreviewHolder> e) throws Exception {
//                                    e.onNext();
//                                }
//                            });
//                            observer.onNext(holder);
//                        }
//                        Flowable<PreviewHolder> flowable = Flowable.create(new FlowableOnSubscribe<PreviewHolder>() {
//                            @Override
//                            public void subscribe(@NonNull FlowableEmitter<PreviewHolder> e) throws Exception {
//                                e.onNext();
//                            }
//                        }, BackpressureStrategy.BUFFER);
//
//                        animDispose = Flowable.just(holder)
//                                .throttleLast(100, TimeUnit.MILLISECONDS)
//                                .subscribeOn(Schedulers.io())
////                        animDispose = Flowable.timer(3000, TimeUnit.MILLISECONDS, Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<PreviewHolder>() {
//                                    @Override
//                                    public void accept(PreviewHolder previewHolder) throws Exception {
//                                        L.e("htextview : " + hTextView);
//                                        L.e("holder : " + holder);
//                                        L.e("position : " + holder.getAdapterPosition());
//                                        hTextView.animateText("第" + holder.getAdapterPosition() + "张");
//                                    }
//                                });
////                                .subscribe(new Consumer<Long>() {
////                                    @Override
////                                    public void accept(Long aLong) throws Exception {
////                                        L.e("htextview : " + hTextView);
////                                        L.e("holder : " + holder);
////                                        L.e("position : " + holder.getAdapterPosition());
////                                        hTextView.animateText("第" + holder.getAdapterPosition() + "张");
////                                    }
////                                });
//                        context.addDispose(animDispose);

                    }
                }
            }
        });

        mAdapter = new PreviewAdapter(context);
        recyclerView.setAdapter(mAdapter);

        mTransmitter.transmit()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PreviewBean>>() {
                    @Override
                    public void accept(List<PreviewBean> previewBeen) throws Exception {
                        mAdapter.setData(previewBeen);
                        if(!Lists.empty(previewBeen)) {
                            hTextView.animateText("第0张");
                        }
                    }
                });
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
