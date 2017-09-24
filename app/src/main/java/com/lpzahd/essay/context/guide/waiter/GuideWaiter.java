package com.lpzahd.essay.context.guide.waiter;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.waiter.TintBarWaiter;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.guide.GuideActivity;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.gallery.Gallery;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class GuideWaiter extends ToneActivityWaiter<GuideActivity> {

    @BindView(R.id.top_bg_drawee_view)
    SimpleDraweeView topBgDraweeView;

    @BindView(R.id.middle_bg_drawee_view)
    SimpleDraweeView middleBgDraweeView;

    @BindView(R.id.title_tv)
    AppCompatTextView titleTv;

    @BindView(R.id.desc_tv)
    AppCompatTextView descTv;

    @BindView(R.id.head_drawee_view)
    SimpleDraweeView headDraweeView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public GuideWaiter(GuideActivity guideActivity) {
        super(guideActivity);
    }

    @Override
    public void init() {
        super.init();
        addWaiter(new TintBarWaiter(context) {
            @Override
            public void setTintResource(SystemBarTintManager manager) {
                manager.setTintResource(R.mipmap.bg_guide_top_one_part);
            }
        });
    }

    @Override
    protected void initView() {
        headDraweeView.setImageURI(Frescoer.res(R.mipmap.ic_head_haughty_rabbit));

        GuideModel[] models = new GuideModel[]{
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
                GuideModel.build(TYPE_ESSAY, Frescoer.res(R.mipmap.ic_module_essay), "随笔", "想写点什么呢"),
                GuideModel.build(TYPE_GALLERY, Frescoer.res(R.mipmap.ic_module_gallery), "相册", "想写点什么呢"),
        };

        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new GuideItemDecoration(2, 20, 20));

        final GuideAdapter adapter = new GuideAdapter(context, Arrays.asList(models));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<GuideHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, GuideHolder guideHolder) {
                final int position = guideHolder.getAdapterPosition();
                GuideModel model = adapter.getItem(position);
                switch (model.type) {
                    case TYPE_ESSAY:
                        EssayActivity.startActivity(context);
                        break;
                    case TYPE_GALLERY:
                        Gallery.it().getConfig().setMode(Gallery.Configuration.MODE_SELECT);
                        Gallery.it().startActivity(context);
//                        GalleryActivity.startActivity(context);
                        break;
                }
            }
        });
    }

    private static final int TYPE_ESSAY = 1;
    private static final int TYPE_GALLERY = 2;

    @IntDef({TYPE_ESSAY, TYPE_GALLERY})
    @Retention(RetentionPolicy.SOURCE)
    private @interface TYPE {
    }

    private static class GuideModel {

        @TYPE
        int type;
        Uri uri;
        String title;
        String desc;

        static GuideModel build(int type, Uri uri, String title, String desc) {
            return new GuideModel(type, uri, title, desc);
        }

        GuideModel(int type, Uri uri, String title, String desc) {
            this.type = type;
            this.uri = uri;
            this.title = title;
            this.desc = desc;
        }
    }

    static class GuideHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.pic_drawee_view)
        SimpleDraweeView picDraweeView;

        @BindView(R.id.title_tv)
        AppCompatTextView titleTv;

        @BindView(R.id.desc_tv)
        AppCompatTextView descTv;

        GuideHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class GuideAdapter extends ToneAdapter<GuideModel, GuideHolder> {

        GuideAdapter(Context context, List<GuideModel> data) {
            super(context, data);
        }

        @Override
        public GuideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflateItemView(R.layout.item_guide, parent);
            return new GuideHolder(itemView);
        }

        @Override
        public void onBindViewHolder(GuideHolder holder, int position) {
            GuideModel model = getItem(position);

            holder.picDraweeView.setImageURI(model.uri);
            holder.titleTv.setText(model.title);
            holder.descTv.setText(model.desc);
        }

    }

    private static class GuideItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpanCount;
        private int mWidthSize;
        private int mHeightSize;

        GuideItemDecoration(int spanCount) {
            this(spanCount, 4, 4);
        }

        GuideItemDecoration(int spanCount, int widthSize, int heightSize) {
            mSpanCount = spanCount;
            mWidthSize = widthSize;
            mHeightSize = heightSize;
        }

        private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                    int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if ((pos + 1) % spanCount == 0) {
                    // 如果是最后一列，则不需要绘制右边
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0) {
                        // 如果是最后一列，则不需要绘制右边
                        return true;
                    }
                } else {
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                        return true;
                }
            }
            return false;
        }

        private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                                  int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                // StaggeredGridLayoutManager 且纵向滚动
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    childCount = childCount - childCount % spanCount;
                    // 如果是最后一行，则不需要绘制底部
                    if (pos >= childCount)
                        return true;
                } else {
                    // StaggeredGridLayoutManager 且横向滚动
                    // 如果是最后一行，则不需要绘制底部
                    if ((pos + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            final int spanCount = mSpanCount;
            int childCount = parent.getAdapter().getItemCount();
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                // 如果是最后一行，则不需要绘制底部
                outRect.set(0, 0, mWidthSize, 0);
            } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                // 如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, mHeightSize);
            } else {
                outRect.set(0, 0, mWidthSize, mHeightSize);
            }
        }

    }

}
