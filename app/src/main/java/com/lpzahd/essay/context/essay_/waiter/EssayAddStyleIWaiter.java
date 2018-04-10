package com.lpzahd.essay.context.essay_.waiter;

import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.tone.waiter.WaiterManager;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.context.MediaSelectActivity;
import com.lpzahd.essay.common.waiter.MediaSelectWaiter;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.tool.MediaTool;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

import com.lpzahd.aop.api.ThrottleFirst;
import com.lpzahd.waiter.consumer.State;

import org.reactivestreams.Subscriber;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayAddStyleIWaiter extends ToneActivityWaiter<EssayAddActivity> implements Transmitter, Receiver {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @BindView(R.id.title_edt)
    AppCompatEditText titleEdt;

    @BindView(R.id.content_edt)
    AppCompatEditText contentEdt;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.record_iv)
    AppCompatImageView recordIv;

    @BindView(R.id.image_iv)
    AppCompatImageView imageIv;

    @BindView(R.id.play_iv)
    AppCompatImageView playIv;

    private Realm mRealm;
    private PicAdapter mAdapter;

    private List<MediaTool.ImageBean> mPicSource;

    public EssayAddStyleIWaiter(EssayAddActivity essayAddActivity) {
        super(essayAddActivity);
    }

    @Override
    protected void init() {
        super.init();
        mRealm = Realm.getDefaultInstance();
        RxTaxi.get().regist(PreviewPicWaiter.TAG, this);
        RxBus.get().regist(MediaSelectWaiter.TAG, this);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if(mRealm != null && !mRealm.isClosed()) mRealm.close();
        RxTaxi.get().unregist(PreviewPicWaiter.TAG);
        RxBus.get().unregist(MediaSelectWaiter.TAG);
    }

    @Override
    protected void initToolBar() {
        toolBar.setTitle("随笔");
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected int createOptionsMenu(Menu menu) {
        context.getMenuInflater().inflate(R.menu.menu_essay_add, menu);
        return State.STATE_TRUE;
    }

    @Override
    protected int optionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save) {
            final String title = titleEdt.getText().toString();
            final String content = contentEdt.getText().toString();
            if(Strings.empty(title) && Strings.empty(content) && Lists.empty(mPicSource)) {
                T.t("...");
            } else {
                Realm realm = Realm.getDefaultInstance();

                final Essay essay = new Essay();
                essay.setTitle(title);
                essay.setContent(content);

                if(!Lists.empty(mPicSource)) {
                    RealmList<Image> images = new RealmList<>();
                    for(int i = 0, size = mPicSource.size(); i < size; i++) {
                        MediaTool.ImageBean bean = mPicSource.get(i);
                        images.add(new Image.Builder()
                                .path(bean.getOriginalPath())
                                .width(bean.getWidth())
                                .height(bean.getHeight())
                                .source(ImageSource.SOURCE_FILE)
                                .suffix(bean.getMimeType())
                                .build());
                    }
                    essay.setImages(images);
                }

                realm.executeTransactionAsync(realm1 -> realm1.copyToRealm(essay), () -> {
                    T.t("新增成功");
                    RxBus.get().post(EssayActivity.TAG, true);
                    context.delayBackpress();
                });

            }
            return State.STATE_TRUE;
        }

        return super.optionsItemSelected(item);
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new PicAdapter(context);
        recyclerView.setAdapter(mAdapter);

        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ToneItemTouchHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<PicHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, PicHolder picHolder) {
                super.onClick(rv, picHolder);
                PreviewPicActivity.startActivity(context);
            }
        });
    }

    @ThrottleFirst
    @OnClick(R.id.image_iv)
    public void openGallery(View view) {
        WaiterManager.single().put(MediaSelectActivity.class, new MediaSelectWaiter(6, new SwipeRefreshWaiter.DataFlowable() {

            @Override
            public int getPage() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Flowable<? extends List> doRefresh(int page) {
                return mRealm.where(Collection.class)
                        .sort("date", Sort.DESCENDING)
                        .findAllAsync()
                        .asFlowable()
                        .filter(RealmResults::isLoaded)
                        .map((Function<RealmResults<Collection>, List>) collections -> {
                            if(Lists.empty(collections)) return Collections.emptyList();

                            List<MediaSelectWaiter.MediaBean> medias = new ArrayList<>(collections.size());
                            for(Collection result : collections) {
                                MediaSelectWaiter.MediaBean bean = new MediaSelectWaiter.MediaBean();
                                Image image = result.getImage();
                                bean.uri = Frescoer.uri(image.getPath(), image.getSource());
                                medias.add(bean);
                            }
                            return medias;
                        });
            }
        }));
        MediaSelectActivity.startActivity(context);
    }

    @Override
    public Flowable<List<PreviewPicWaiter.PreviewBean>> transmit() {
        if(Lists.empty(mPicSource)) return null;
        return Flowable.just(mPicSource)
                .map(mediaBeen -> {
                    List<PreviewPicWaiter.PreviewBean> pics = new ArrayList<>();
                    for (int i = 0, size = mediaBeen.size(); i < size; i++) {
                        PreviewPicWaiter.PreviewBean pic = new PreviewPicWaiter.PreviewBean();
                        pic.uri = Frescoer.uri(mediaBeen.get(i).getOriginalPath(), ImageSource.SOURCE_FILE);
                        pics.add(pic);
                    }
                    return pics;
                });
    }

    @Override
    public void receive(Flowable flowable) {
        flowable.subscribe((Consumer<List<MediaSelectWaiter.MediaBean>>) mediaBeen -> {
            List<PicBean> pics = new ArrayList<>(mediaBeen.size());
            for (int i = 0, size = mediaBeen.size(); i < size; i++) {
                PicBean pic = new PicBean();
                pic.uri = mediaBeen.get(i).uri;
                pics.add(pic);
            }
            mAdapter.setData(pics);

            mPicSource = new ArrayList<>(mediaBeen.size());
            for (int i = 0, size = mediaBeen.size(); i < size; i++) {
                MediaTool.ImageBean bean = new MediaTool.ImageBean();
                final String path = Frescoer.parseUri(mediaBeen.get(i).uri);
                try {
                    ExifInterface exifInterface = new ExifInterface(path);
                    int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                    int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                    bean.setWidth(width);
                    bean.setHeight(height);
                    bean.setMimeType(MimeTypeMap.getFileExtensionFromUrl(path));
                    bean.setOriginalPath(path);
                } catch (IOException e) {
                    L.e(e.getMessage());
                }
                mPicSource.add(bean);
            }
        });
    }

    public static class PicBean {
        public Uri uri;
    }

    public static class PicHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        public PicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PicAdapter extends ToneAdapter<PicBean, PicHolder> {

        private int size;

        public PicAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 56);
        }

        @Override
        public PicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PicHolder(inflateItemView(R.layout.item_essay_add_pic, parent));
        }

        @Override
        public void onBindViewHolder(PicHolder holder, int position) {
            PicBean bean = getItem(position);
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
}
