package com.lpzahd.common.tone.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Desction: recyclerview 点击处理
 * Author: Lpzahd
 * Date: 2016/12/9 0009
 */

public class OnItemChildTouchListener<T extends RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGes;

    private RecyclerView rv;

    private View itemView;

    private float pointX;
    private float pointY;

    private GestureDetector.OnGestureListener gesListner = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (itemView != null) {
                onClick(rv, (T) rv.getChildViewHolder(itemView), findChildViewUnder(itemView, pointX, pointY));
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (itemView != null) {
                onLongClick(rv, (T) rv.getChildViewHolder(itemView), findChildViewUnder(itemView, pointX, pointY));
            }
        }

    };

    public OnItemChildTouchListener(RecyclerView recyclerView) {
        rv = recyclerView;
        mGes = new GestureDetector(recyclerView.getContext(), gesListner);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGes.onTouchEvent(e);
        pointX = e.getX();
        pointY = e.getY();
        itemView = rv.findChildViewUnder(e.getX(), e.getY());
        return false;
    }

    private View findChildViewUnder(View v, float x, float y) {
        if(checkViewByPoint(v, x, y)) {
            if(v instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) v;

                for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                    final View child = parent.getChildAt(i);
                    if(checkViewByPoint(child, x - parent.getLeft(), y - parent.getTop())) {
                        View target = findChildViewUnder(child, x - parent.getLeft(), y - parent.getTop());
                        if(target != null) return target;
                    }
                }

            }
            return v;
        }
        return null;
    }

    private boolean checkViewByPoint(View v, float x, float y) {
        final float translationX = ViewCompat.getTranslationX(v);
        final float translationY = ViewCompat.getTranslationY(v);
        return x >= v.getLeft() + translationX &&
                x <= v.getRight() + translationX &&
                y >= v.getTop() + translationY &&
                y <= v.getBottom() + translationY;
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
    public void onClick(RecyclerView rv, T t, View child) {

    }

    /**
     * 长按
     *
     * @param rv recyclerView
     * @param t  recyclerView.holder
     */
    public void onLongClick(RecyclerView rv, T t, View child) {

    }
    
}
