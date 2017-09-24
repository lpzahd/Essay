package com.lpzahd.gallery.presenter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.atool.enmu.Image;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.gallery.R;
import com.lpzahd.gallery.R2;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.tool.MediaTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class MultiSelectPresenter extends ToneActivityWaiter<GalleryActivity> {

    private static int MODE_SINGLE = 0;
    private static int MODE_MULTI = 1;

    private final int LIMIT = 30;

    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefershLayout;

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R2.id.folder_name_tv)
    AppCompatTextView folderNameTv;

    @BindView(R2.id.preview_tv)
    AppCompatTextView previewTv;

    @BindView(R2.id.boottom_bar_layout)
    RelativeLayout boottomBarLayout;

    @BindView(R2.id.root_view_layout)
    RelativeLayout rootViewLayout;

    private int mode = MODE_SINGLE;

    private int select = 1;

    private MultiAdapter adapter;

    private SwipeRefreshWaiter mRefreshWaiter;

    public MultiSelectPresenter(GalleryActivity activity, int select) {
        super(activity);

        if (select < 0) {
            throw new IllegalArgumentException("二逼不解释！");
        }

        if (select == 1)
            mode = MODE_SINGLE;
        else
            mode = MODE_MULTI;
    }

    @Override
    protected void setContentView() {
        context.setContentView(R.layout.activity_multi_select);
    }

    @Override
    protected void initView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        adapter = new MultiAdapter(context, getScreenSize(context).widthPixels / 3);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {
        addWindowWaiter(mRefreshWaiter = new SwipeRefreshWaiter(swipeRefershLayout, recyclerView) {

            @Override
            public Flowable<? extends List> doRefresh(final int page) {
                return  Flowable.create(new FlowableOnSubscribe<List<MediaTool.MediaBean>>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<List<MediaTool.MediaBean>> e) throws Exception {
                        List<MediaTool.MediaBean> mediaBeanList = MediaTool.getMediaFromContext(context, String.valueOf(Integer.MIN_VALUE), page, LIMIT);
                        e.onNext(mediaBeanList);
                    }
                }, BackpressureStrategy.BUFFER)
                        .map(new Function<List<MediaTool.MediaBean>, List<MultiBean>>() {
                            @Override
                            public List<MultiBean> apply(@NonNull List<MediaTool.MediaBean> mediaBeen) throws Exception {
                                List<MultiBean> res = new ArrayList<>();
                                for (int i = 0, size = mediaBeen.size(); i < size; i++) {
                                    MultiBean bean = new MultiBean();
                                    bean.uri = Frescoer.uri(mediaBeen.get(i).getOriginalPath(), Image.SOURCE_FILE);
                                    res.add(bean);
                                }
                                return res;
                            }
                        })
                        .subscribeOn(Schedulers.io());
            }

        });

        mRefreshWaiter.autoRefresh();

    }

    private static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    private static class MultiBean {
        public Uri uri;
    }

    private class MultiAdapter extends ToneAdapter<MultiBean, MultiHolder> {

        private int size = 200;

        private MultiAdapter(Context context, int size) {
            super(context);
            this.size = size;
        }

        @Override
        public MultiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MultiHolder(inflateItemView(R.layout.item_media_select_grid, parent));
        }

        @Override
        public void onBindViewHolder(MultiHolder holder, int position) {
            MultiBean bean = getItem(position);
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

    static class MultiHolder extends ToneAdapter.ToneHolder {

        @BindView(R2.id.image_drawee_view)
        SimpleDraweeView imageDraweeView;

        @BindView(R2.id.check_box)
        AppCompatCheckBox checkBox;

        MultiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
