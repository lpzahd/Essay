package com.lpzahd.gallery.a3d;

import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL11;

public abstract class Layer {
    float mX = 0f;
    float mY = 0f;
    float mWidth = 0;
    float mHeight = 0;
    boolean mHidden = false;

    public final float getX() {
        return mX;
    }

    public final float getY() {
        return mY;
    }

    public final void setPosition(float x, float y) {
        mX = x;
        mY = y;
    }

    public final float getWidth() {
        return mWidth;
    }

    public final float getHeight() {
        return mHeight;
    }

    public final void setSize(float width, float height) {
        if (mWidth != width || mHeight != height) {
            mWidth = width;
            mHeight = height;
            onSizeChanged();
        }
    }

    public boolean isHidden() {
        return mHidden;
    }

    public void setHidden(boolean hidden) {
        if (mHidden != hidden) {
            mHidden = hidden;
            onHiddenChanged();
        }
    }

    public abstract void generate(RenderView view, RenderView.Lists lists);

    // Returns true if something is animating.
    public boolean update(RenderView view, float frameInterval) {
        return false;
    }

    public void renderOpaque(RenderView view, GL11 gl) {
    }

    public void renderBlended(RenderView view, GL11 gl) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    // Allows subclasses to further constrain the hit test defined by layer
    // bounds.
    public boolean containsPoint(float x, float y) {
        return true;
    }

    protected void onSurfaceCreated(RenderView view, GL11 gl) {
    }

    protected void onSizeChanged() {
    }

    protected void onHiddenChanged() {
    }
}
