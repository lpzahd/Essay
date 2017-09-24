package com.lpzahd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.drawable.RoundedColorDrawable;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 */
public class Divider extends View {

    private Drawable mDivider;

    public Divider(Context context) {
        this(context, null);
    }

    public Divider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Divider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Divider(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Divider);

        int divider = ta.getInt(R.styleable.Divider_drawableId, 0);

        if(divider == 0) {
            divider  = ta.getInt(R.styleable.Divider_colorId, 0);
        }

        if(divider == 0) {
            mDivider = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.divider));
        } else {
            mDivider = ContextCompat.getDrawable(getContext(), divider);
        }

        ta.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final int left = getPaddingLeft();
        final int right = getWidth() - getPaddingRight();
        final int top = getPaddingTop();
        final int bottom = top + getHeight();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(canvas);
    }

    public void setDivider(@DrawableRes int id) {
        setDivider(ContextCompat.getDrawable(getContext(), id));
    }

    public void setDivider(Drawable drawable) {
        if(mDivider == drawable) return ;

        mDivider = drawable;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(
//                getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec),
//                getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

}
