package com.lpzahd.animview;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public abstract class Drawer {

    private int width;
    private int height;

    public void preDraw(AnimView view) {
        width = view.getWidth();
        height = view.getHeight();
    }

    public abstract void drawStart(Canvas canvas);

    public abstract void drawEnd(Canvas canvas);

    public abstract void drawAnim(Canvas canvas, float pro);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean interceptTouchEvent() {
        return false;
    }

    public boolean onTouchEvent(AnimView view, MotionEvent event) {
        return false;
    }

}
