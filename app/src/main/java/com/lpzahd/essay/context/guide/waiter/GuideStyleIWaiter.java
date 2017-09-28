package com.lpzahd.essay.context.guide.waiter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.waiter.TintBarWaiter;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.guide.GuideActivity;
import com.lpzahd.essay.context.leisure.LeisureActivity;
import com.lpzahd.gallery.Gallery;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : 此处I 代表 罗马数字
 */
public class GuideStyleIWaiter extends ToneActivityWaiter<GuideActivity> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private GuideAdapter mAdapter;

    @Override
    protected void init() {
        super.init();
//        addWaiter(new TintBarWaiter(context));
    }

    public GuideStyleIWaiter(GuideActivity guideActivity) {
        super(guideActivity);
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new GuideAdapter(context);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<GuideHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, GuideHolder guideHolder) {
                final int position = guideHolder.getAdapterPosition();
                GuideModel model = mAdapter.getItem(position);
                switch (model.id) {
                    case 0:
                        EssayActivity.startActivity(context);
                        break;
                    case 1:
                        Gallery.it().getConfig().setMode(Gallery.Configuration.MODE_SELECT);
                        Gallery.it().startActivity(context);
//                        GalleryActivity.startActivity(context);
                        break;
                    case 2:
                        Gallery.it().getConfig().setMode(Gallery.Configuration.MODE_GALLERY);
                        Gallery.it().startActivity(context);
//                        GalleryActivity.startActivity(context);
                        break;
                    case 3:
                        LeisureActivity.startActivity(context);
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        GuideModel[] models = new GuideModel[] {
                new GuideModel(0, "随笔", "解落三秋叶,能开二月花", Color.parseColor("#7EDCFD"), Color.parseColor("#FDBDD9")),
                new GuideModel(1, "图片", "解落三秋叶,能开二月花", Color.parseColor("#FFA894"), Color.parseColor("#88FEE5")),
                new GuideModel(2, "音乐", "解落三秋叶,能开二月花", Color.parseColor("#E089F9"), Color.parseColor("#FFD452")),
                new GuideModel(3, "视频", "解落三秋叶,能开二月花", Color.parseColor("#888AF2"), Color.parseColor("#62FAD7")),
        };
        mAdapter.setData(Arrays.asList(models));
    }

    private static class GuideModel {
        private int id;
        private String title;
        private String desc;
        private int startColor;
        private int endColor;

        public GuideModel(int id, String title, String desc, int startColor, int endColor) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.startColor = startColor;
            this.endColor = endColor;
        }
    }

    static class GuideHolder extends ToneAdapter.ToneHolder {

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

        GuideAdapter(Context context) {
            super(context);
        }

        @Override
        public GuideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GuideHolder(inflateItemView(R.layout.item_guide_style_01, parent));
        }

        @Override
        public void onBindViewHolder(GuideHolder holder, int position) {
            GuideModel model = getItem(position);
            holder.titleTv.setText(model.title);
            holder.descTv.setText(model.desc);

            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {
                    model.startColor, model.endColor
            });
            drawable.setCornerRadius(Ui.dip2px(context, 12));
            holder.itemView.setBackground(drawable);

        }
    }

}
