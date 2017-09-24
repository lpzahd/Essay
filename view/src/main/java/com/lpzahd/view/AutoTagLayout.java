package com.lpzahd.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class AutoTagLayout extends ViewGroup {

    /**
     * 每行之间默认的高度
     */
    private static final int DEFAULT_ROW = 30;

    /**
     * 每列之间默认的宽度
     */
    private static final int DEFAULT_COL = 16;

    private int mRow = DEFAULT_ROW;

    // 不知道怎么处理了
    private int mCol = DEFAULT_COL;

    ViewManager manager;

    /**
     * 添加textview到最后一个
     */
    public void addTextView(TextView view) {
        addView(view, -1);
    }

    /**
     * 添加textview到最后指定位置
     */
    public void addTextView(TextView view, int index) {
        addView(view, index);
        requestLayout();
    }

    public void addTextViews(TextView... tvs) {
        for (TextView tv : tvs) {
            addView(tv);
        }
        requestLayout();
    }

    public void setLineSpace(int lineSpace) {
        mRow = lineSpace;
    }

    public AutoTagLayout(Context context) {
        super(context);
    }

    public AutoTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoTagLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getChildTotalHeight() {
        int height = 0;
        if (manager != null) {
            List<LineViews> lines = manager.getLineViews();
            final int size = lines.size();
            for (int i = 0; i < size; i++) {
                height += lines.get(i).getMaxHeight();
                height += mRow;
            }
        }
        return height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (manager == null) {
            manager = new ViewManager(mWidth);
        }
        manager.clear();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final LayoutParams lp = child.getLayoutParams();
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, this.getPaddingLeft() + this.getPaddingRight(), lp.width),
                    getChildMeasureSpec(heightMeasureSpec, this.getPaddingTop() + this.getPaddingBottom(), lp.height)
            );

            manager.addView(child, child.getMeasuredWidth(), child.getMeasuredHeight());
        }

        int totalHeight = getChildTotalHeight();
        if (totalHeight > 0) {
            mHeight = totalHeight;
        }

        setMeasuredDimension(mWidth, mHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        List<LineViews> lineViews = manager.getLineViews();
        final int lineViewsCount = lineViews.size();
        int currHeight = 0;
        for (int i = 0; i < lineViewsCount; i++) {
            LineViews lineView = lineViews.get(i);
            layoutChild(lineView, currHeight);

            // 文字高
            currHeight += lineView.getMaxHeight();

            // 行间距
            currHeight += mRow;
        }
    }

    private void layoutChild(LineViews lineView, int currHeight) {
        int dx = DEFAULT_COL;
        int left = dx;
        for (int i = 0; i < lineView.getChildCount(); i++) {
            View child = lineView.getChild(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            child.layout(left, currHeight, left + width, currHeight + height);

            left += width + dx;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 管理所有view
     */
    private class ViewManager {
        List<LineViews> lineViews = new ArrayList<>();

        private LineViews currentLineView = new LineViews(0);
        private int maxWidth;

        ViewManager(int maxWidth) {
            this.maxWidth = maxWidth;
            init();
        }

        private void init() {
            addLineView(currentLineView);
        }

        private void addLineView(LineViews lineView) {
            lineViews.add(lineView);
        }

        List<LineViews> getLineViews() {
            return lineViews;
        }

        void clear() {
            lineViews.clear();
            currentLineView = new LineViews(0);
            addLineView(currentLineView);
        }

        void addView(@NonNull View v, int width, int height) {
            int totalWidth = currentLineView.getTotalWidth();

            if (maxWidth < (totalWidth + width)) {
                // 换行
                currentLineView = new LineViews(lineViews.size());
                addLineView(currentLineView);
            }
            // 添加的view 没有超过总长度
            currentLineView.addView(v, width, height);
        }
    }


    /**
     * 保存每一行views
     */
    private class LineViews {

        /**
         * 自备行号
         */
        int mLine;

        int maxHeight;
        int maxWidth;
        int totalWidth;

        int currTotalWidth;
        LinkedList<View> views = new LinkedList<View>();

        /**
         * line >= 0
         *
         * @param line 第几行
         */
        LineViews(int line) {
            mLine = line;
        }

        public int getLine() {
            return mLine;
        }

        void addView(@NonNull View view, int width, int height) {
            views.add(view);
            maxHeight = Math.max(maxHeight, height);
            maxWidth = Math.max(maxWidth, width);
            totalWidth = currTotalWidth + width;

            currTotalWidth = totalWidth;
        }

        int getMaxHeight() {
            return maxHeight;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        int getTotalWidth() {
            return totalWidth;
        }

        View getChild(int index) {
            return views.get(index);
        }

        int getChildCount() {
            return views.size();
        }

    }
}
