package com.lpzahd.essay.context.essay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lpzahd.atool.constant.Constance;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.essay.R;
import com.lpzahd.essay.app.App;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 迪 on 2016/9/24.
 */

public class EssayPop {

    private final static int tune = Ui.dip2px(App.getApp(), 8);

    private final Handler mHandler = new Handler();

    private PopupWindow popup;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void showEssayPop(View anchor) {
        if (popup == null) {
            newPopup(anchor);
        }

        if (!popup.isShowing()) {
            RecyclerView recyclerView = (RecyclerView) popup.getContentView().findViewById(R.id.recycler_view);
            recyclerView.clearAnimation();
            showAutoLocation(anchor, popup);
        }
    }

    public void dismiss() {
        if (popup != null && popup.isShowing()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    popup.dismiss();
                }
            });
        }
    }

    private void newPopup(View anchor) {
        final Context context = anchor.getContext();
        popup = new PopupWindow();
        // 设置宽高，不然不显示
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置背景，不然setOutsideTouchable 无效
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        // 获取焦点，不然recycler的item直接获取点击事件有所冲突
        popup.setFocusable(true);

        View contentView = View.inflate(context, R.layout.pop_essay, null);
        popup.setContentView(contentView);

        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
        if (listener != null) {
            recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<EssayPopHolder>(recyclerView) {
                @Override
                public void onClick(RecyclerView rv, EssayPopHolder essayPopHolder) {
                    super.onClick(rv, essayPopHolder);
                    listener.onClick(rv, essayPopHolder.getAdapterPosition());
                }

                @Override
                public void onLongClick(RecyclerView rv, EssayPopHolder essayPopHolder) {
                    super.onLongClick(rv, essayPopHolder);
                    listener.onLongClick(rv, essayPopHolder.getAdapterPosition());
                }
            });
        }

        EssayPopAdapter popAdapter = new EssayPopAdapter(context, initEssayPopData());
        recyclerView.setAdapter(popAdapter);
    }

    private void showAutoLocation(View anchor, PopupWindow popup) {
        PopupWindowCompat.setOverlapAnchor(popup, true);
        int[] locations = getPopLocation(anchor, popup.getContentView());
        popup.showAsDropDown(anchor, locations[0], locations[1]);
    }

    private List<EssayPopBean> initEssayPopData() {
        List<EssayPopBean> beans = new ArrayList<>();
        EssayPopBean tag = generateBean(R.drawable.ic_bookmark_black_24dp, "标签");
        beans.add(tag);
        EssayPopBean icon = generateBean(R.drawable.ic_insert_photo_black_24dp, "图片");
        beans.add(icon);
        EssayPopBean isVis = generateBean(R.drawable.ic_visibility_off_black_24dp, "隐藏");
        beans.add(isVis);
        EssayPopBean save = generateBean(R.drawable.ic_file_download_black_24dp, "保存");
        beans.add(save);
        EssayPopBean copy = generateBean(R.drawable.ic_content_copy_black_24dp, "拷贝");
        beans.add(copy);
        EssayPopBean delete = generateBean(R.drawable.ic_delete_black_24dp, "删除");
        beans.add(delete);

        return beans;
    }

    /**
     * 1. 底部排版 （默认）
     * 2. 顶部排版
     * 3. 底部向上位移一半视图距离排版
     * 4. 顶部向下位移一半视图距离排版
     */
    private int[] getPopLocation(View anchor, View popView) {
        Rect rect = new Rect();
        // 获取anchor 在winodw上的位置信息(显示的部分)
        anchor.getGlobalVisibleRect(rect);

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int h = popView.getMeasuredHeight();
        final int w = popView.getMeasuredWidth();

        if (rect.bottom + h < Constance.SCREEN_HEIGHT) {
            // 底部可以铺满
            return new int[]{
                    Constance.SCREEN_WIDTH - w, rect.height() - tune
            };
        }

        if (rect.top - h > 0) {
            // 顶部可以铺满
            return new int[]{
                    Constance.SCREEN_WIDTH - w, -h + tune
            };
        }

        if (rect.bottom + h / 2 < Constance.SCREEN_HEIGHT) {
            // 底部一半可以铺满
            return new int[]{
                    Constance.SCREEN_WIDTH - w, rect.height() / 2 - tune
            };
        }

        if (rect.top - h / 2 > 0) {
            // 顶部一半可以铺满
            return new int[]{
                    Constance.SCREEN_WIDTH - w, rect.height() / 2 - h + tune
            };
        }

        return new int[]{
                Constance.SCREEN_WIDTH - w, rect.height() - tune
        };

    }

    private EssayPopBean generateBean(@DrawableRes int id, String desc) {
        EssayPopBean bean = new EssayPopBean();
        bean.iconId = id;
        bean.desc = desc;
        return bean;
    }

    private class EssayPopBean {
        int iconId;
        String desc;
    }

    private class EssayPopAdapter extends ToneAdapter<EssayPopBean, EssayPopHolder> {

        EssayPopAdapter(Context context, List<EssayPopBean> data) {
            super(context, data);
        }

        @Override
        public EssayPopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflateItemView(R.layout.item_essay_pop, parent);
            return new EssayPopHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EssayPopHolder holder, int position) {
            EssayPopBean bean = getItem(position);
            holder.iconIv.setImageResource(bean.iconId);
            holder.descTv.setText(bean.desc);
        }
    }

    public class EssayPopHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.icon_iv)
        AppCompatImageView iconIv;

        @BindView(R.id.desc_tv)
        AppCompatTextView descTv;

        EssayPopHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {

        void onClick(RecyclerView rv, int position);

        void onLongClick(RecyclerView rv, int position);
    }

    public static class OnItemClick implements OnItemClickListener {

        @Override
        public void onClick(RecyclerView rv, int position) {

        }

        @Override
        public void onLongClick(RecyclerView rv, int position) {

        }
    }
}
