package com.lpzahd.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by Administrator on 2016/11/9 0009.
 *
 * 无限翻转 cardview
 *
 * 已知问题，photoview 请求拦截了事件分发，操蛋子，找不到解决方案，也不想重写photoview， 就缩放会响应了
 */

public class FlipView extends FrameLayout {

    /**
     * 动画属性
     */
    private String propertyName = "rotationY";

    private ObjectAnimator fontAnim;
    private ObjectAnimator backAnim;
    private AnimatorSet animSet;

    // 开启翻转动画
    private boolean isFlip = true;

    // 显示child的角标
    private int showChildIndex = 0;

    private long animDuration = 500L;

//    private long animFastDuration = 160L;

    public FlipListener listener;

    public void setFlipListener(FlipListener listener) {
        this.listener = listener;
    }

    private GestureDetector gestureDetector;

    public FlipView(Context context) {
        this(context, null);
    }

    public FlipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                startFlip(FlipView.this);
                return true;
            }

        });

        this.setClickable(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }


    public void setFlip(boolean isFlip) {
        this.isFlip = isFlip;
    }

    /**
     * 重置
     */
    public void reset() {
        prepare(0);
    }

    /**
     * 如果子view有动态添加，则先调用此方法prepare
     */
    public void prepare() {
       prepare(showChildIndex);
    }

    /**
     * @param showIndex 显示child 的角标
     */
    public void prepare(int showIndex) {
        showChildIndex = showIndex;
        getChildAt(showChildIndex).setAlpha(1);

        float scale = getResources().getDisplayMetrics().density * 10000;
        for (int i = 0; i< getChildCount(); i++) {
            getChildAt(i).setCameraDistance(scale);
            if(showChildIndex != i) {
                getChildAt(i).setAlpha(0);
            }
        }
    }

    public int getShowIndex() {
        return showChildIndex;
    }

    /**
     * 开始翻转
     */
    public void startFlip(View v) {
        if(!isFlip)
            return ;

        if(getChildCount() < 2)
            return ;

        View fontView = getChildAt(showChildIndex);

        View backView = getChildAt(getBackIndex());

        if(animSet != null && animSet.isRunning() ) {
            // 动画没停止拒绝再次开启动画
            return ;
        }
        // 这个效果不好
//        // 快速翻过
//        if(animSet != null && animSet.isRunning() ) {
//            animSet.setDuration(animFastDuration).start();
//            return ;
//        }
//
//        // 重置时间
//        if(animSet != null && animSet.getDuration() != animDuration) {
//            animSet.setDuration(animDuration);
//        }

        flipTowView(fontView, backView);
    }

    /**
     * 获取后面的view的角标
     */
    private int getBackIndex() {
        if(showChildIndex + 1 >= getChildCount())
            return 0;
        else
           return showChildIndex + 1;
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        if(fontAnim == null) {
            fontAnim = new ObjectAnimator();
            fontAnim.setFloatValues(0, 90);
            fontAnim.setPropertyName(propertyName);
        }

        if(backAnim == null) {
            backAnim = new ObjectAnimator();
            backAnim.setFloatValues(-90, 0);
            backAnim.setPropertyName(propertyName);
        }

        if(animSet == null) {
            animSet = new AnimatorSet();
            animSet.play(backAnim).after(fontAnim);
            animSet.setDuration(animDuration);
        }
    }

    /**
     * 翻转
     */
    private void flipTowView(final View fontView, final View backView) {
        initAnim();
        fontAnim.setTarget(fontView);
        backAnim.setTarget(backView);
        fontAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fontView.setAlpha(0);
                fontView.setRotationY(0);
                animation.removeListener(this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animation.removeListener(this);
            }
        });

        backAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                backView.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.removeListener(this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animation.removeListener(this);
            }
        });

        animSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fontView.setClickable(false);
                backView.setClickable(false);

                if(listener != null) {
                    listener.onStart(FlipView.this, showChildIndex, getBackIndex());
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                backView.setClickable(true);

                if(listener != null) {
                    listener.onEnd(FlipView.this, showChildIndex, getBackIndex());
                }

                showChildIndex = getBackIndex();
                animation.removeListener(this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);

                if(listener != null) {
                    listener.onCancel(FlipView.this);
                }
                animation.removeListener(this);
            }
        });

        animSet.start();

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("onSaveInstanceState", super.onSaveInstanceState());
        bundle.putInt("index", showChildIndex);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        showChildIndex = bundle.getInt("index");

        try{
            // 由于view id 有一致的情况，也会报错
            super.onRestoreInstanceState(state);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        prepare();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        clearAnim(animSet);
        animSet = null;

        clearAnim(backAnim);
        backAnim = null;

        clearAnim(fontAnim);
        fontAnim = null;

        listener = null;
    }

    /**
     * 释放动画资源
     */
    private void clearAnim(Animator anim) {
        if(anim != null && anim.isRunning()) {
            anim.cancel();
            anim.removeAllListeners();
        }
    }

    public interface FlipListener {
        void onStart(View v, int sIndex, int eIndex);
        void onEnd(View v, int sIndex, int eIndex);
        void onCancel(View v);
    }

    //    final class ViewPropertyAnimatorOnSubscribe implements Observable.OnSubscribe<Void> {
//
//        final ViewPropertyAnimator anim;
//
//        ViewPropertyAnimatorOnSubscribe(ViewPropertyAnimator animator) {
//            this.anim = animator;
//        }
//
//        @Override public void call(final Subscriber<? super Void> subscriber) {
//            checkUiThread();
//
//            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    if (!subscriber.isUnsubscribed()) {
//                        subscriber.onNext(null);
//                    }
//                }
//            };
//
//            anim.setListener(listener);
//            anim.start();
//
//            subscriber.add(new MainThreadSubscription() {
//                @Override protected void onUnsubscribe() {
//                    anim.setListener(null);
//                    anim.cancel();
//                }
//            });
//        }
//    }
}
