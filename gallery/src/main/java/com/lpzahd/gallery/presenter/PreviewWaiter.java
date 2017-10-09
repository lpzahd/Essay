package com.lpzahd.gallery.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.gallery.R;
import com.lpzahd.gallery.R2;
import com.lpzahd.gallery.context.PreviewActivity;
import com.lpzahd.view.FlipView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * 作者 : 迪
 * 时间 : 2017/10/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class PreviewWaiter extends ToneActivityWaiter<PreviewActivity> {

    private static final String EXTRA_DATA_MEDIA = "extra_data_media";
    private static final String EXTRA_DATA_CURR_INDEX = "extra_data_curr_index";

    @BindView(R2.id.tool_bar)
    Toolbar toolBar;

    @BindView(R2.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    private int maxSize = 1;
    private int selectSize = 0;

    /**
     * 会溢出
     */
    public static void startActivity(Context context, int currIndex, ArrayList<PreviewBean> medias) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(EXTRA_DATA_CURR_INDEX, currIndex);
        intent.putParcelableArrayListExtra(EXTRA_DATA_MEDIA, medias);
        context.startActivity(intent);
    }

    // 保留
//    public static void startActivity(Activity activity, View v1, View v2,int currIndex, ArrayList<PreviewBean> medias) {
//        Intent intent = new Intent(activity, PreviewActivity.class);
//        intent.putExtra(EXTRA_DATA_CURR_INDEX, currIndex);
//        intent.putParcelableArrayListExtra(EXTRA_DATA_MEDIA, medias);
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
//    }

    private int currIndex;
    private ArrayList<PreviewBean> mMedias;
    private PreviewAdapter mAdapter;

    public PreviewWaiter(PreviewActivity previewActivity) {
        super(previewActivity);
    }

    @Override
    protected void init() {
        super.init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            context.getWindow().setExitTransition(new Explode());
        }
    }

    @Override
    protected void setContentView() {
        context.setContentView(R.layout.activity_preview);
    }

    @Override
    protected boolean checkArgus(Intent intent) {
        currIndex = intent.getIntExtra(EXTRA_DATA_CURR_INDEX, 0);
        mMedias = intent.getParcelableArrayListExtra(EXTRA_DATA_MEDIA);
        return super.checkArgus(intent);
    }

    @Override
    protected void initToolBar() {
        toolBar.setTitle("图片");
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        mAdapter = new PreviewAdapter(context);
        mAdapter.setData(mMedias);
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(currIndex);
    }

    public static class PreviewBean implements Parcelable {
        public Uri uri;
        public boolean checked;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.uri, flags);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        }

        public PreviewBean() {
        }

        protected PreviewBean(Parcel in) {
            this.uri = in.readParcelable(Uri.class.getClassLoader());
            this.checked = in.readByte() != 0;
        }

        public static final Parcelable.Creator<PreviewBean> CREATOR = new Parcelable.Creator<PreviewBean>() {
            @Override
            public PreviewBean createFromParcel(Parcel source) {
                return new PreviewBean(source);
            }

            @Override
            public PreviewBean[] newArray(int size) {
                return new PreviewBean[size];
            }
        };
    }

    private class PreviewAdapter extends ToneAdapter<PreviewBean, PreviewHolder> {

        private int size = 200;
        private List<Integer> slects = new ArrayList<>();

        private PreviewAdapter(Context context) {
            super(context);
        }

        private PreviewAdapter(Context context, int size) {
            super(context);
            this.size = size;
            slects = new ArrayList<>();
        }

        public List<Integer> getSlects() {
            return slects;
        }

        public void setSlects(List<Integer> slects) {
            this.slects = slects;
        }

        @Override
        public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final PreviewHolder holder = new PreviewHolder(inflateItemView(R.layout.item_preview, parent));
            holder.setCheckBoxClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    PreviewBean bean = getItem(position);
                    if (bean.checked) {
                        bean.checked = false;
                        slects.remove(Integer.valueOf(position));
                        selectSize = slects.size();
                    } else {
                        if (slects.size() >= maxSize) {
                            T.t("你最多只能选择" + maxSize + "张照片");
                            holder.checkBox.setChecked(false);
                        } else {
                            bean.checked = true;
                            slects.add(position);
                            selectSize = slects.size();
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(PreviewHolder holder, int position) {
            PreviewBean bean = getItem(position);
            holder.checkBox.setChecked(bean.checked);

            showPhoto(holder.photoDraweeView, bean);
            showBlurImg(holder.bgDraweeView, bean);

            holder.flipView.reset();
            holder.story.setText("" + position);
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

        private void showBlurImg(SimpleDraweeView bgDraweeView, PreviewBean bean) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(bean.uri)
                    .setPostprocessor(new BlurPostprocessor(context))
                    .build();

            PipelineDraweeController controller =
                    (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(bgDraweeView.getController())
                            .build();

            bgDraweeView.setController(controller);
        }
    }

    static class PreviewHolder extends ToneAdapter.ToneHolder {

        @BindView(R2.id.check_box)
        AppCompatCheckBox checkBox;

        @BindView(R2.id.photo_drawee_view)
        PhotoDraweeView photoDraweeView;

        @BindView(R2.id.bg_layout)
        ViewGroup bgLayout;

        @BindView(R2.id.bg_drawee_view)
        SimpleDraweeView bgDraweeView;

        @BindView(R2.id.story)
        AppCompatTextView story;

        @BindView(R2.id.flip_view)
        FlipView flipView;

        PreviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            longClickToFlip(photoDraweeView);
            longClickToFlip(bgLayout);
        }

        void setCheckBoxClickListener(View.OnClickListener listener) {
            if (listener != null)
                checkBox.setOnClickListener(listener);
        }

        void longClickToFlip(View v) {
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    flipView.startFlip();
                    return false;
                }
            });
        }

    }
}
