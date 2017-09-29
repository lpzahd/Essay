package com.lpzahd.atool.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lpzahd.base.NoInstance;

import java.lang.reflect.Field;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Ui extends NoInstance {

    @SuppressWarnings("unchecked")
    public static <V extends View> V findViewById(View v, int id) {
        return (V) v.findViewById(id);
    }

    /**
     * 截图
     */
    public static Bitmap getDrawingCache(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName 文件名
     * @param c            文件类型
     * @return 文件id
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 打开输入法
     */
    public static void showInput(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 隐藏输入法
     */
    public static void hideInput(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getColor(Context context, @ColorRes int id) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int id) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColorStateList(id);
        } else {
            return context.getResources().getColorStateList(id);
        }
    }

    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static void setBackground(@NonNull View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(drawable);
        } else {
            v.setBackgroundDrawable(drawable);
        }
    }
}
