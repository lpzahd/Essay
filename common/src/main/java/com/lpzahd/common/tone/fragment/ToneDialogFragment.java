package com.lpzahd.common.tone.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class ToneDialogFragment extends DialogFragment {

    protected String TAG = this.getClass().getName();

    public void show(AppCompatActivity act) {
        show(act, TAG);
    }

    public void show(AppCompatActivity act, String tag) {
        show(act.getFragmentManager(), tag);
    }

    /**
     * 满屏window
     */
    protected void setMaxWindow() {
        Window window = getDialog().getWindow();
        if(window == null) return ;

        final WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }

    protected void setWidth(int width) {
        setSize(width, -3);
    }

    protected void setHeight(int height) {
        setSize(-3, height);
    }

    protected void setSize(int width, int height) {
        Window window = getDialog().getWindow();
        if(window == null) return ;

        final WindowManager.LayoutParams params = window.getAttributes();
        if(width >= -2)
            params.width = width;
        if(height >= -2)
            params.height = height;
        getDialog().getWindow().setAttributes(params);
    }

    protected void setWidthPercent(@FloatRange(from = 0.0f, to = 1.0f) float w) {
        setSizePercent(w, 0);
    }

    protected void setHeightPercent(@FloatRange(from = 0.0f, to = 1.0f) float h) {
        setSizePercent(0, h);
    }

    protected void setSizePercent(@FloatRange(from = 0.0f, to = 1.0f) float w, @FloatRange(from = 0.0f, to = 1.0f) float h) {
        Window window = getDialog().getWindow();
        if(window == null) return ;

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        final WindowManager.LayoutParams params = window.getAttributes();
        if(w != 0)
            params.width = (int) (dm.widthPixels * w);
        if(h != 0)
            params.height = (int) (dm.heightPixels * h);
        getDialog().getWindow().setAttributes(params);
    }

    public Context getContext() {
        return getActivity();
    }

}
