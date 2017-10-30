package com.lpzahd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.lpzahd.Lists;
import com.lpzahd.atool.ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 作者 : 迪
 * 时间 : 2017/10/28.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class KangNaView extends View {

    private TxtBitmapControl mControl;

    private int mWidth;
    private int mHeight;

    private boolean animationRunning = false;

    // 单独每个动画的执行时间
    private int duration = 400;
    // 速率
    private int frameRate = 10;
    private int timer = 0;
    // 受动画影响的位置
    private int position;

    private Handler canvasHandler;

    private Bitmap[] mBitmaps;

    private OnKangNaCompleteListener onKangNaCompleteListener;

    public void setOnKangNaCompleteListener(OnKangNaCompleteListener onKangNaCompleteListener) {
        this.onKangNaCompleteListener = onKangNaCompleteListener;
    }

    public Bitmap textToBitmap(String txt) {
        Bitmap bitmap = Bitmap.createBitmap(120, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(0, 60);
        canvas.drawColor(Color.BLUE);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 0, 0));
        paint.setTextSize(20);

        paint.setColor(Color.RED);
        StaticLayout layout = new StaticLayout(txt, paint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        layout.draw(canvas);
        canvas.save();
        return bitmap;
    }

    public KangNaView(Context context) {
        this(context, null);
    }

    public KangNaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KangNaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KangNaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        canvasHandler = new Handler();
    }

    public void setTexts(boolean anim, String... texts) {
        final int width = getWidth();
        final int height = getHeight();

        if(width <= 0 || height <=0 )
            throw new IllegalStateException("KangNaView必须有固定的尺寸！");

        final int txtWidth = width / 5;
        final int txtHeight = height / 3;

        Canvas canvas = new Canvas();
        canvas.drawColor(Color.BLUE);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(txtHeight/2);
        paint.setFakeBoldText(true);
        paint.setColor(Color.WHITE);

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (txtHeight - fontMetrics.bottom - fontMetrics.top) / 2;

//        StaticLayout layout = new StaticLayout(texts[0], paint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
//        layout.draw(canvas);

        Bitmap[] bitmaps = new Bitmap[texts.length];
        for(int i = 0; i < texts.length; i++) {
            Bitmap bitmap = Bitmap.createBitmap(txtWidth, txtHeight, Bitmap.Config.ARGB_8888);
            bitmaps[i] = bitmap;
            canvas.setBitmap(bitmap);
            canvas.drawText(texts[i], txtHeight/2, baseline, paint);
        }

        setBitmaps(anim, bitmaps);
    }

    public void setBitmaps(boolean anim, Bitmap... bitmaps) {
        recycleBitmaps();

        mBitmaps = bitmaps;
        if(anim)
            anim();
         else
            postInvalidate();
    }

    private void recycleBitmaps() {
        if(mBitmaps == null)
            return;

        for(Bitmap b : mBitmaps) {
            if(!b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }
        mBitmaps = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getWidth();
        mHeight = getHeight();
    }

    Matrix matrix = new Matrix();
    Paint mPaint = new Paint();
    Camera mCamera = new Camera();

    public void anim() {
        timer = 0;
        position = 0;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mBitmaps == null || mBitmaps.length == 0)
            return ;

        BitmapLayout[] layouts = bitmapLayout(mBitmaps);

        final int length = mBitmaps.length;

        final float percent = ((float) timer * frameRate) / duration;

        if(percent > 1) {
            position++;
            timer = 0;
            matrix.reset();
        }

        // 绘制受动画影响之前的bitmap
        for(int i = 0; i < position; i++) {
            canvas.drawBitmap(mBitmaps[i], layouts[i].left, layouts[i].top, null);
        }

        if(position >= length)  {
            if (onKangNaCompleteListener != null)
                onKangNaCompleteListener.onComplete(this);
            return;
        }

        mCamera.save();
        mCamera.rotateY(180 * (1 - percent));
        mCamera.getMatrix(matrix);
        mCamera.restore();

        int alpha = (int) (255 * percent);
        mPaint.setAlpha(alpha);

        matrix.preTranslate(-layouts[position].bitmap.getWidth()/2, -layouts[position].bitmap.getHeight()/2);
        matrix.postTranslate(layouts[position].bitmap.getWidth()/2, layouts[position].bitmap.getHeight()/2);

        // 翻转bitmap
        canvas.save();
        canvas.translate(layouts[position].left, layouts[position].top);
        canvas.drawBitmap(mBitmaps[position], matrix, mPaint);
        canvas.restore();

        timer++;

        canvasHandler.postDelayed(runnable, frameRate);

    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * 图片排列
     */
    private BitmapLayout[] bitmapLayout(Bitmap[] bitmaps) {
        BitmapLayout[] layouts = new BitmapLayout[bitmaps.length];

        final int heigth = mHeight;
        final int width = mWidth;
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int bitmapTotalWidth = getTotalWidth(bitmaps);

        int spacing = 0;
        if(bitmaps.length > 1) {
            spacing = (width - bitmapTotalWidth - paddingLeft - paddingRight) / (bitmaps.length + 1);
        }

        int marginLeft = paddingLeft;
        for(int i = 0; i < bitmaps.length; i++) {
            Bitmap b = bitmaps[i];
            BitmapLayout layout = new BitmapLayout();
            layout.bitmap = b;
            layout.left = marginLeft + spacing;
            layout.right = layout.left + b.getWidth();
            layout.top = (heigth - paddingTop - paddingBottom - b.getHeight()) / 2;
            layout.bottom = layout.top + b.getHeight();
            layout.centerX = layout.left + b.getWidth() / 2;
            layout.centerY = layout.top + b.getHeight() / 2;
            layouts[i] = layout;

            marginLeft = layout.right;
        }

        return layouts;
    }

    private int getTotalWidth(Bitmap... bitmaps) {
        int totalWidth = 0;
        for(Bitmap b : bitmaps) {
            totalWidth += b.getWidth();
        }
        return totalWidth;
    }

    private int getMaxHeight(Bitmap... bitmaps) {
        int maxHeight = 0;
        for(Bitmap b : bitmaps) {
            maxHeight = Math.max(maxHeight, b.getHeight());
        }
        return maxHeight;
    }

    public interface OnKangNaCompleteListener {
        void onComplete(KangNaView kangNaView);
    }

    private static class BitmapLayout {

        Bitmap bitmap;
        int left;
        int right;
        int top;
        int bottom;
        int centerX;
        int centerY;
    }


    private static class TxtBitmapControl {

        static List<String[]> txts = new ArrayList<>();

        static {
            txts.add(new String[] {
                    "△", "▽", "△", "▽","△"
            });

            txts.add(new String[] {
                    "1", "+", "2", "=", "?"
            });

            txts.add(new String[] {
                    "→", "↓", "↑", "↓", "←"
            });
        }

        private Random random = new Random();

        public String[] random() {
            return txts.get(random.nextInt(txts.size()));
        }

    }

}