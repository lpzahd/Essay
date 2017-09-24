package com.lpzahd.animview.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.lpzahd.animview.AnimView;
import com.lpzahd.animview.Drawer;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class SearchToLineDrawer extends Drawer {

    /**
     * 间距
     */
    private static final float PADDING = 8f;

    /**
     * 搜索尾线相对于 圈半径的 比率
     * len(尾线的长度) = r（圆半径） *  n(比率)
     */
    private static final float LINE_TO_CIR_RATIO = 1.0f;

    private Paint paint;

    /**
     * 最大偏移距离
     */
    private float offsetWidth;

    /**
     * 外圆和内院的间距
     */
    private float cr;

    /**
     * 放大镜圆形
     */
    private RectF cRect = new RectF();

    /**
     * 外圆
     */
    private RectF wRect = new RectF();

    /**
     * 动态 放大镜圆形
     */
    private RectF cSRect = new RectF();

    /**
     * 动态 外圆
     */
    private RectF wSRect = new RectF();

    private float sign = 0.707f;

    private GestureDetector ges;

    private AnimView view;

    public SearchToLineDrawer(Context context) {
        GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (view.getState() == AnimView.STATE_ANIM_END) {
                    view.reverseAnim();
                }
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (view.getState() == AnimView.STATE_ANIM_START)
                    return super.onSingleTapUp(e);

                final float downX = e.getX();
                final float downY = e.getY();
                if (view.getState() == AnimView.STATE_ANIM_NONE) {
                    if (isInner(wRect, downX, downY)) {
                        view.setDuration(AnimView.DURATION_NORMAL);
                        view.startAnim();
                    }
                }

                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (view.getState() != AnimView.STATE_ANIM_END) {
                    if (isInner(wRect, e.getX(), e.getY())) {
                        view.setDuration(AnimView.DURATION_SHORT);
                        view.startAnim();
                    }
                }
                return super.onDoubleTap(e);
            }
        };
        ges = new GestureDetector(context, listener);

        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
    }

    @Override
    public void preDraw(AnimView view) {
        super.preDraw(view);
        this.view = view;
        view.setClickable(true);

        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;

        cr = halfHeight / 2;
        float cw = cr * 0.414f;

        float dx = LINE_TO_CIR_RATIO * cr * sign;

        cRect.left = (float) halfWidth - cr;
        cRect.right = (float) halfWidth + cr;
        cRect.top = (float) halfHeight - cr;
        cRect.bottom = (float) halfHeight + cr;
        cRect.offset(-dx / 2, -dx / 2);

        wRect.left = cRect.left - cw;
        wRect.right = cRect.right + dx + cw;
        wRect.top = cRect.top - cw;
        wRect.bottom = cRect.bottom + dx + cw;

        cSRect.set(cRect);
        wSRect.set(wRect);

        offsetWidth = halfWidth - wSRect.width() * 0.5f - PADDING;
    }

    @Override
    public void drawStart(Canvas canvas) {
        drawAnim(canvas, 0);
    }

    @Override
    public void drawEnd(Canvas canvas) {
        offsetHoriRect(-offsetWidth);
        drawAnim(canvas, 1.0f);
    }

    @Override
    public void drawAnim(Canvas canvas, float pro) {
        if (paint == null) return;

        final float left = cSRect.left + cr * (1 + sign);
        final float right = left + LINE_TO_CIR_RATIO * cr * sign;
        final float top = cSRect.top + cr * (1 + sign);
        final float bottom = top + LINE_TO_CIR_RATIO * cr * sign;
        canvas.drawLine(left, top, right, bottom, paint);

        canvas.drawArc(cSRect, 0, 360, false, paint);
        canvas.drawArc(wSRect, 90, 360 * (1 - pro), false, paint);

        final float dx = offsetWidth * pro;
        final float ix = wSRect.width() / 2 * (1 - pro);
        canvas.drawLine(wSRect.left + ix, wSRect.bottom,
                (wSRect.right + 2 * dx - ix), wSRect.bottom, paint);

        offsetHoriRect(-dx);
    }

    private void offsetHoriRect(float offset) {
        cSRect.left = cRect.left + offset;
        cSRect.right = cRect.right + offset;

        wSRect.left = wRect.left + offset;
        wSRect.right = wRect.right + offset;
    }

    @Override
    public boolean interceptTouchEvent() {
        return true;
    }

    @Override
    public boolean onTouchEvent(AnimView view, MotionEvent event) {
        return ges.onTouchEvent(event);
    }

    private boolean isInner(RectF rectF, float x, float y) {
        return rectF.left < x && rectF.right > x && rectF.top < y && rectF.bottom > y;
    }
}
