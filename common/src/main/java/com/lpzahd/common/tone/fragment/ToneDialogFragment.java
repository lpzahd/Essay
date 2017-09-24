package com.lpzahd.common.tone.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
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
        final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }

    public Context getContext() {
        return getActivity();
    }

}
