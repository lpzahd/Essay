package com.lpzahd.essay.context.essay_.waiter;

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
import com.lpzahd.atool.ui.L;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.PreviewPicActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    public static final String TAG = "com.lpzahd.essay.context.essay_.waiter.PreviewPicWaiter";

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
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = snapHelper.findSnapView(mLayoutManager);
                    if(snapView != null) {
                        PreviewHolder holder = (PreviewHolder) recyclerView.getChildViewHolder(snapView);

                        if(animDispose != null && !animDispose.isDisposed()) {
                            animDispose.dispose();
                        }

                        final WeakReference<PreviewHolder> holderRef = new WeakReference<>(holder);
                        animDispose = Flowable.timer(200, TimeUnit.MILLISECONDS, Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        if(holderRef.get() != null)
                                            hTextView.animateText("第" + holderRef.get().getAdapterPosition() + "张");
                                    }
                                });
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
