package com.lpzahd.common.tone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Desction: recyclerview 点击处理
 * Author: Lpzahd
 * Date: 2016/12/9 0009
 */

public class OnItemHolderTouchListener<T extends RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGes;

    public RecyclerView rv;

    private View itemView;

    private GestureDetector.OnGestureListener gesListner = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (itemView != null) {
                onClick(rv, (T) rv.getChildViewHolder(itemView));
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (itemView != null) {
                onLongClick(rv, (T) rv.getChildViewHolder(itemView));
            }
        }

    };

    public OnItemHolderTouchListener(RecyclerView recyclerView) {
        rv = recyclerView;
        mGes = new GestureDetector(recyclerView.getContext(), gesListner);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGes.onTouchEvent(e);
        itemView = rv.findChildViewUnder(e.getX(), e.getY());
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * 点击
     *
     * @param rv recyclerView
     * @param t  recyclerView.holder
     */
    public void onClick(RecyclerView rv, T t) {

    }

    /**
     * 长按
     *
     * @param rv recyclerView
     * @param t  recyclerView.holder
     */
    public void onLongClick(RecyclerView rv, T t) {

    }
}
