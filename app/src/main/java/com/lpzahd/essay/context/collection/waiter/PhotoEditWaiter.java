package com.lpzahd.essay.context.collection.waiter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.PhotoEditActivity;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;
import com.lpzahd.gallery.tool.MediaTool;
import com.lpzahd.gallery.waiter.PreviewWaiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/13 17:14
 * @change
 */
public class PhotoEditWaiter extends ToneActivityWaiter<PhotoEditActivity> implements View.OnClickListener {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    @BindView(R.id.private_drawee_view)
    SimpleDraweeView privateDraweeView;

    @BindView(R.id.desc_tv)
    AppCompatTextView descTv;

    private SinglePicWaiter.PicAdapter mAdapter;

    private Disposable mMediaDisposable;
    // 原数据
    private List<MediaTool.MediaBean> mMediaBeans;
    // 展示中的数据
    private SinglePicWaiter.PicBean mDisplayBean;

    public PhotoEditWaiter(PhotoEditActivity photoEditActivity) {
        super(photoEditActivity);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void initView() {
        super.initView();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new SinglePicWaiter.PicAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<SinglePicWaiter.PicHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, SinglePicWaiter.PicHolder picHolder) {
                super.onClick(rv, picHolder);
                displayPhoto(picHolder.getAdapterPosition());
            }

        });

        queryMedias();

        RxView.clicks(descTv)
                .throttleFirst(500L, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        addPrivatePhotoOrNot();
                    }
                });
    }

    private void displayPhoto(int position) {
        mDisplayBean = mAdapter.getItem(position);

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setOldController(zoomableDraweeView.getController())
                .setUri(mDisplayBean.uri)
                .setTapToRetryEnabled(true)
                .build();
        zoomableDraweeView.setController(draweeController);

        MediaTool.MediaBean media = mMediaBeans.get(position);
        descTv.setText(new StringBuilder()
                .append("path : ").append(media.getOriginalPath())
                .append("\n")
                .append("bucket : ").append(media.getBucketDisplayName())
                .append("\n")
                .append("mime : ").append(media.getMimeType())
                .append("\n")
                .append("width : ").append(media.getWidth())
                .append("\n")
                .append("height : ").append(media.getHeight())
                .append("\n")
                .append("createdate : ").append(media.getCreateDate())
                .append("\n")
                .append("modifydate : ").append(media.getModifiedDate())
                .toString());
    }

    private void addPrivatePhotoOrNot() {
        if(descTv.isSelected()) {

        } else {

        }
    }

    /**
     * 获取媒体数据 并加载
     */
    private void queryMedias() {
        mMediaDisposable = Flowable.create(new FlowableOnSubscribe<List<MediaTool.MediaBean>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<MediaTool.MediaBean>> e) throws Exception {
                List<MediaTool.MediaBean> mediaBeanList = MediaTool.getImageFromContext(context, MediaTool.MEDIA_NO_BUCKET);
                e.onNext(mediaBeanList);
            }
        }, BackpressureStrategy.BUFFER)
                .filter(new Predicate<List<MediaTool.MediaBean>>() {
                    @Override
                    public boolean test(@NonNull List<MediaTool.MediaBean> mediaBeen) throws Exception {
                        return !Lists.empty(mediaBeen);
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Function<List<MediaTool.MediaBean>, List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public List<SinglePicWaiter.PicBean> apply(List<MediaTool.MediaBean> mediaBeans) throws Exception {
                        mMediaBeans = mediaBeans;
                        List<SinglePicWaiter.PicBean> pics = new ArrayList<>(mediaBeans.size());
                        for (MediaTool.MediaBean media : mediaBeans) {
                            SinglePicWaiter.PicBean pic = new SinglePicWaiter.PicBean();
                            pic.uri = Frescoer.uri(media.getOriginalPath(), ImageSource.SOURCE_FILE);
                            pics.add(pic);
                        }
                        return pics;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SinglePicWaiter.PicBean>>() {
                    @Override
                    public void accept(List<SinglePicWaiter.PicBean> picBeans) throws Exception {
                        mAdapter.setData(picBeans);
                        displayPhoto(0);
                    }
                });
        context.addDispose(mMediaDisposable);
    }

}
