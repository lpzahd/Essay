package com.lpzahd.essay.context.essay_.waiter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
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
import com.lpzahd.aop.api.ThrottleFirst;
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
import com.lpzahd.common.tone.waiter.WaiterManager;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.context.MediaSelectActivity;
import com.lpzahd.essay.common.waiter.MediaSelectWaiter;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.gallery.tool.MediaTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class EssayAddComponent  implements Transmitter, Receiver<List<MediaSelectWaiter.MediaBean>> {

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

    private final Context context;
    private Unbinder unbinder;
    private View layout;

    private String id;


    public EssayAddComponent(Context context) {
        this.context = context;
    }

    public void init() {
        mRealm = Realm.getDefaultInstance();
        RxTaxi.get().regist(PreviewPicWaiter.TAG, this);
        RxBus.get().regist(MediaSelectWaiter.TAG, this);
    }

    public void destroy() {
        if(mRealm != null && !mRealm.isClosed()) mRealm.close();
        RxTaxi.get().unregist(PreviewPicWaiter.TAG);
        RxBus.get().unregist(MediaSelectWaiter.TAG);
        unbinder.unbind();
    }

    public View inflate(ViewGroup viewGroup) {
        if(layout != null) {
            return layout;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = inflater.inflate(R.layout.content_essay_add, viewGroup, false);
        unbinder = ButterKnife.bind(this, layout);

        stepup();
        return layout;
    }

    private void stepup() {
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

    public void update(String essayId) {
        Essay target = mRealm.where(Essay.class)
                .equalTo("id", essayId)
                .findFirst();

        if(target == null) return;

        final String title = target.getTitle();
        final String content = target.getContent();
        final RealmList<Image> images = target.getDefaultImages();
        List<EssayAddComponent.PicBean> pics = new ArrayList<>(images.size());
        for (int i = 0, size = images.size(); i < size; i++) {
            EssayAddComponent.PicBean pic = new EssayAddComponent.PicBean();
            pic.uri = Frescoer.uri(images.get(i).getPath(), ImageSource.SOURCE_FILE);
            pics.add(pic);
        }

        this.id = essayId;
        update(title, content, pics);
    }

    public void update(String title, String content, List<PicBean> pics) {
        Ui.setText(titleEdt, title);
        Ui.setText(contentEdt, content);

        mAdapter.setData(pics);

        if(!Lists.empty(pics)) {
            mPicSource = new ArrayList<>(pics.size());
            for (int i = 0, size = pics.size(); i < size; i++) {
                MediaTool.ImageBean bean = new MediaTool.ImageBean();
                final String path = Frescoer.parseUri(pics.get(i).uri);
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
        }
    }

    @ThrottleFirst
    @OnClick(R.id.image_iv)
    public void openGallery() {
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

    public boolean update() {
        final String title = titleEdt.getText().toString();
        final String content = contentEdt.getText().toString();
        if(Strings.empty(title) && Strings.empty(content) && Lists.empty(mPicSource)) {
            T.t("...");
            return false;
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                final Essay essay = realm.where(Essay.class)
                            .equalTo("id", id)
                            .findFirst();

                if (essay != null) {
                    realm.beginTransaction();
                    essay.setTitle(title);
                    essay.setContent(content);

                    if(!Lists.empty(mPicSource)) {
                        RealmList<Image> images = new RealmList<>();
                        for(int i = 0, size = mPicSource.size(); i < size; i++) {
                            MediaTool.ImageBean bean = mPicSource.get(i);
                            Image image = new Image.Builder()
                                    .path(bean.getOriginalPath())
                                    .width(bean.getWidth())
                                    .height(bean.getHeight())
                                    .source(ImageSource.SOURCE_FILE)
                                    .suffix(bean.getMimeType())
                                    .build();
                            images.add(realm.copyToRealm(image));
                        }
                        essay.setImages(images);
                    }
                    realm.commitTransaction();
                    T.t("修改成功");
                    RxBus.get().post(EssayActivity.TAG, true);
                    return true;
                } else {
                    return false;
                }

            }
        }
    }


    public boolean save() {
        final String title = titleEdt.getText().toString();
        final String content = contentEdt.getText().toString();
        if(Strings.empty(title) && Strings.empty(content) && Lists.empty(mPicSource)) {
            T.t("...");
            return false;
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
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

                realm.beginTransaction();
                realm.copyToRealm(essay);
                realm.commitTransaction();
                T.t("新增成功");
                RxBus.get().post(EssayActivity.TAG, true);
                return true;
            }
        }
    }

    @Override
    public Flowable<List<PreviewPicWaiter.PreviewBean>> transmit() {
        if(Lists.empty(mPicSource))
            return Flowable.just(Collections.emptyList());

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

    @SuppressLint("CheckResult")
    @Override
    public void receive(Flowable<List<MediaSelectWaiter.MediaBean>> flowable) {
        flowable.subscribe(mediaBeen -> {
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

        PicAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 56);
        }

        @Override
        public PicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PicHolder(inflateItemView(R.layout.item_essay_add_pic, parent));
        }

        @Override
        public void onBindViewHolder(@NonNull PicHolder holder, int position) {
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
