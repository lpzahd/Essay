package com.lpzahd.essay.context.collection.waiter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.lpzahd.Codec;
import com.lpzahd.Lists;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.CollectionActivity;
import com.lpzahd.essay.context.collection.CollectionEditActivity;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.fresco.zoomable.DoubleTapGestureListener;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;
import com.lpzahd.gallery.tool.MediaTool;

import org.threeten.bp.Instant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import io.realm.Realm;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/13 17:14
 * @change
 */
public class CollectionEditWaiter extends ToneActivityWaiter<CollectionEditActivity> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.zoomable_drawee_view)
    ZoomableDraweeView zoomableDraweeView;

    @BindView(R.id.desc_tv)
    AppCompatTextView descTv;

    private SinglePicWaiter.PicAdapter mAdapter;

    // 原数据
    private List<MediaTool.MediaBean> mMediaBeans;
    // 展示中的数据
    private int mDisplayPosition;

    private Realm mRealm;

    private int mDeleteTimes;

    public CollectionEditWaiter(CollectionEditActivity collectionEditActivity) {
        super(collectionEditActivity);
    }

    @Override
    protected void init() {
        super.init();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void initView() {
        super.initView();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new SinglePicWaiter.PicAdapter(context) {
            @Override
            public void onSwiped(final int position) {
                final List<SinglePicWaiter.PicBean> data = getData();
                if(data.isEmpty()) return;

                final MediaTool.MediaBean mediaBean = mMediaBeans.get(position);
                final SinglePicWaiter.PicBean picBean = data.get(position);

                mMediaBeans.remove(position);
                remove(position);

                // 最多提醒三次
                if(mDeleteTimes > 3) {
                    if(mDisplayPosition == position) {
                        if(position < getItemCount()) {
                            displayPhoto(position);
                        } else {
                            int index = getItemCount() - 1;
                            displayPhoto(index < 0 ? 0 : index);
                        }
                    }

                    Files.delete(mediaBean.getOriginalPath());
                    Ui.scanSingleMedia(context, new File(mediaBean.getOriginalPath()));
                    return;
                }


                String name = new File(mediaBean.getOriginalPath()).getName();
                new MaterialDialog.Builder(context)
                        .title("删除图片")
                        .content("图片" + name + "将被移除，确定？")
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .negativeText(R.string.tip_negative)
                        .positiveText(R.string.tip_positive)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                mDeleteTimes++;
                                if(mDisplayPosition == position) {
                                    if(position < getItemCount()) {
                                        displayPhoto(position);
                                    } else {
                                        int index = getItemCount() - 1;
                                        displayPhoto(index < 0 ? 0 : index);
                                    }
                                }

                                Files.delete(mediaBean.getOriginalPath());
                                Ui.scanSingleMedia(context, new File(mediaBean.getOriginalPath()));

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                                mMediaBeans.add(position, mediaBean);
                                add(position, picBean);
                            }
                        })
                        .show();
            }
        };
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<SinglePicWaiter.PicHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, SinglePicWaiter.PicHolder picHolder) {
                super.onClick(rv, picHolder);
                displayPhoto(picHolder.getAdapterPosition());
            }

        });

        ToneItemTouchHelperCallback touchCallback = new ToneItemTouchHelperCallback(mAdapter);
        touchCallback.setCanDrag(false);
        touchCallback.setCanSwipe(true);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(touchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        queryMedias();

        zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView) {
            @Override
            public void onLongPress(MotionEvent e) {

                if(Lists.empty(mMediaBeans)) {
                    T.t("图库空空如也");
                    return ;
                }

                MediaTool.MediaBean mediaBean = mMediaBeans.get(mDisplayPosition);
                showCollectionDialog(new File(mediaBean.getOriginalPath()).getName());
            }
        });
    }

    private void displayPhoto(int position) {
        mDisplayPosition = position;
        SinglePicWaiter.PicBean displayBean = mAdapter.getItem(position);

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setOldController(zoomableDraweeView.getController())
                .setUri(displayBean.uri)
                .setAutoPlayAnimations(true)
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

    private void showCollectionDialog(String name) {
        new MaterialDialog.Builder(context)
                .title("收藏图片")
                .content("图片" + name + "将被收藏，原图会被移除，确定？")
                .negativeText(R.string.tip_negative)
                .positiveText(R.string.tip_positive)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {

                        boolean collect = collectPhoto(mMediaBeans.get(mDisplayPosition));
                        if(collect) {
                            mMediaBeans.remove(mDisplayPosition);
                            mAdapter.remove(mDisplayPosition);
                            displayPhoto(mDisplayPosition);
                        }
                    }
                })
        .show();
    }

    private boolean collectPhoto(MediaTool.MediaBean mediaBean) {
        String md5 = Codec.md5Hex(mediaBean.getOriginalPath());
        // 查询
        Collection photo = mRealm.where(Collection.class)
                .equalTo("MD5", md5)
//                .or()
//                .equalTo("originalPath", mediaBean.getOriginalPath())
                .findFirst();
        if(photo == null) {
            // 文件复制操作
            String destPath = copy(mediaBean.getOriginalPath());
            // 插入
            mRealm.beginTransaction();
            Collection collection = new Collection();
            collection.setTrans(true);
            collection.setCount(1);
            collection.setOriginalPath(mediaBean.getOriginalPath());
            collection.setMD5(md5);
            Image image = new Image();
            image.setPath(destPath);
            image.setWidth(mediaBean.getWidth());
            image.setHeight(mediaBean.getHeight());
            image.setSource(ImageSource.SOURCE_FILE);
            image.setSuffix(mediaBean.getMimeType());
            collection.setImage(image);
            mRealm.copyToRealm(collection);
            mRealm.commitTransaction();

            Files.delete(mediaBean.getOriginalPath());
            T.t("图片收藏成功！");
            Ui.scanSingleMedia(context, new File(mediaBean.getOriginalPath()));
            return true;
        } else {
            Image image = photo.getImage();
            L.e("image : " + image);
            // 修改
            mRealm.beginTransaction();
            photo.setCount(photo.getCount() + 1);
            mRealm.commitTransaction();
            T.t("图片曾经被收藏过！");
            return false;
        }
    }

    /**
     * 获取媒体数据 并加载
     */
    private void queryMedias() {
        Disposable mMediaDisposable = Flowable.create(new FlowableOnSubscribe<List<MediaTool.MediaBean>>() {
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

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }

    @Override
    protected int backPressed() {
        RxBus.get().post(CollectionActivity.TAG, true);
        return super.backPressed();
    }

    private String copy(String source) {
        return Files.copyToScope(source, Files.Scope.PHOTO_COLLECTION, String.valueOf(Instant.now().toEpochMilli()));
    }

}
