package com.lpzahd.atool.action;

import android.content.Context;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ScrollerCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.lpzahd.atool.ui.L;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ) 好像实现不了
 */
@Deprecated
public class OverTouchEventAction {

    private View mActionView;
    private View mTouchView;

    private final int mTouchSlop;

    private ScrollerCompat mScroller;
    private VelocityTracker mVelocityTracker;

    private int mLastMotionY;
    private int mActivePointerId;

    public OverTouchEventAction(View actionView, View touchView) {
        mActionView = actionView;
        mTouchView = touchView;

        final Context context = touchView.getContext();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = ScrollerCompat.create(context);
    }

    public void overTouch() {
        mTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int actionMasked = event.getActionMasked();

                if (actionMasked == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
                    return false;
                }

                if(mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);

                switch (actionMasked) {
                    case MotionEvent.ACTION_DOWN:
                        mLastMotionY = (int) event.getY();
                        mActivePointerId = event.getPointerId(0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                        if (activePointerIndex == -1) {
                            L.e("Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                            break;
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return false;
            }
        });
    }

    private boolean dispatchNestedPreScroll() {
        return true;
    }

}
