package com.lpzahd.essay.context.essay_.waiter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.aop.api.ThrottleFirst;
import com.lpzahd.atool.enmu.Image;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.tone.adapter.OnItemChildTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.R2;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.tool.MediaTool;
import com.lpzahd.waiter.agency.ActivityWaiter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayAddStyleIWaiter extends ToneActivityWaiter<EssayAddActivity> {

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

    private PicAdapter mAdapter;

    public EssayAddStyleIWaiter(EssayAddActivity essayAddActivity) {
        super(essayAddActivity);
    }

    @Override
    protected void initToolBar() {
        toolBar.setTitle("随笔");
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new PicAdapter(context);
        recyclerView.setAdapter(mAdapter);

        final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ToneItemTouchHelperCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new OnItemChildTouchListener<PicHolder>(recyclerView) {

            @Override
            public void onClick(RecyclerView rv, PicHolder picHolder, View child) {
                super.onClick(rv, picHolder, child);
            }
        });
    }

    @ThrottleFirst
    @OnClick(R.id.image_iv)
    public void openGallery(View view) {
        Gallery.with(context)
                .image()
                .maxSize(6)
                .subscribe(new Receiver<List<MediaTool.MediaBean>>() {
                    @Override
                    public void receive(Flowable<List<MediaTool.MediaBean>> flowable) {
                        flowable.subscribe(new Consumer<List<MediaTool.MediaBean>>() {
                            @Override
                            public void accept(List<MediaTool.MediaBean> mediaBeen) throws Exception {
                                List<PicBean> pics = new ArrayList<>();
                                for(int i = 0, size = mediaBeen.size(); i < size; i++) {
                                    PicBean pic = new PicBean();
                                    pic.uri = Frescoer.uri(mediaBeen.get(i).getOriginalPath(), Image.SOURCE_FILE);
                                    pics.add(pic);
                                }
                                mAdapter.setData(pics);
                            }
                        });
                    }
                })
                .openGallery();
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
