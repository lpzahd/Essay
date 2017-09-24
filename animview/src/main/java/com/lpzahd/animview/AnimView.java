package com.lpzahd.animview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.lpzahd.animview.paint.SearchToLineDrawer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class AnimView extends View implements ViewTreeObserver.OnPreDrawListener {

    public static final long DURATION_LONG = 3000L;
    public static final long DURATION_NORMAL = 1500L;
    public static final long DURATION_SHORT = 500L;

    private static final float DEFAULT_ANIM_STARTF = 0;
    private static final float DEFAULT_ANIM_ENDF = 1L;

    public static final int STATE_ANIM_NONE = 0;
    public static final int STATE_ANIM_START = 1;
    public static final int STATE_ANIM_END = 2;

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        if(drawer != null)
            drawer.preDraw(this);
        return true;
    }

    @IntDef({STATE_ANIM_NONE,STATE_ANIM_START, STATE_ANIM_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    private Drawer drawer;

    private ValueAnimator animator;

    private float pro;

    /**
     * 是否是反转动画
     */
    private boolean isReverse = false;

    AnimListener listener;

    public void setAnimListener(AnimListener listener) {
        this.listener = listener;
    }

    @State
    protected int state = STATE_ANIM_NONE;

    @State
    public int getState() {
        return state;
    }

    public AnimView(Context context) {
        this(context, null);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()    {
        this.drawer = new SearchToLineDrawer(getContext());
        animator = ValueAnimator.ofFloat(DEFAULT_ANIM_STARTF, DEFAULT_ANIM_ENDF)
                .setDuration(DURATION_NORMAL);
        attachAnimator(animator);
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    public void setDuration(long duration) {
        if(animator != null)
            animator.setDuration(duration);
    }

    public void setSearchViewPaint(@NonNull Drawer paint) {
        this.drawer = paint;
        invalidate();
    }

    public ValueAnimator getAnimator() {
        return animator;
    }

    public void setAnimator(ValueAnimator animator) {
        this.animator = animator;
        attachAnimator(animator);
    }

    private ValueAnimator attachAnimator(ValueAnimator animator) {
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(updateListener);
        animator.addListener(animListener);
        return animator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawer != null) {
            switch (state) {
                case STATE_ANIM_START:
                    drawer.drawAnim(canvas, pro);
                    break;
                case STATE_ANIM_END:
                    drawer.drawEnd(canvas);
                    break;
                case STATE_ANIM_NONE:
                    drawer.drawStart(canvas);
                    break;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        attachAnimator(animator);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        stopAnim();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        recycle();
    }

    public void startAnim() {
        if(animator != null && !animator.isStarted())
            animator.start();

        state = STATE_ANIM_START;
    }

    public void stopAnim() {
        if(animator != null)
            animator.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pause() {
        if(animator != null && !animator.isPaused())
            animator.pause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resume() {
        if(animator != null && animator.isPaused())
            animator.resume();
    }

    public void reverseAnim() {
        if(animator == null) return;

        animator.reverse();
        isReverse = true;
        state = STATE_ANIM_START;
    }

    public void reset() {
        if(animator.isStarted())
            animator.cancel();

        state = STATE_ANIM_NONE;
    }

    private void recycle() {
        reset();
        animator.removeAllListeners();
    }

    ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            pro = (float) animation.getAnimatedValue();
            invalidate();

            if(listener != null) {
                listener.onAnimRunning(AnimView.this, state, pro);
            }
        }
    };

    Animator.AnimatorListener animListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
            Log.e("hit", "onAnimationStart");
            super.onAnimationStart(animation);
            if(listener != null) {
                listener.onAnimStart(AnimView.this, state);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.e("hit", "onAnimationEnd");
            super.onAnimationEnd(animation);
            if(isReverse)
                state = STATE_ANIM_NONE;
            else
                state = STATE_ANIM_END;

            isReverse = false;

            if(listener != null) {
                listener.onAnimEnd(AnimView.this, state);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(drawer != null && drawer.interceptTouchEvent())
            drawer.onTouchEvent(this, event);

        return super.onTouchEvent(event);
    }

    public interface AnimListener {

        void onAnimStart(AnimView view, int state);

        void onAnimRunning(AnimView view, int state, float pro);

        void onAnimEnd(AnimView view, int state);

    }
}
