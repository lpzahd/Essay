package com.lpzahd.essay.context.essay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.lpzahd.Strings;
import com.lpzahd.aop.api.Log;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.fragment.ToneDialogFragment;
import com.lpzahd.essay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 迪 on 2016/9/17.
 */

public class EssayStyleIIAddDialog extends ToneDialogFragment {

    @BindView(R.id.positive)
    AppCompatTextView positive;

    @BindView(R.id.negative)
    AppCompatTextView negative;

    @BindView(R.id.essay_parent)
    LinearLayout essayParent;

    @BindView(R.id.title_edt)
    AppCompatEditText titleEdt;

    @BindView(R.id.content_edt)
    AppCompatEditText contentEdt;

    InputCallback inputCallback;

    Unbinder mUnbinder;

    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Dialog dialog = getDialog();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCanceledOnTouchOutside(false);

        View rootView = inflater.inflate(R.layout.dialog_essay_add_style_02, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setBackground(essayParent);
        initView();

        return rootView;
    }

    /**
     * 配置圆角背景
     */
    private void setBackground(View view) {
        final Context context = getContext();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(context.getResources().getDimension(R.dimen.corner_radius_d4));
        drawable.setColor(Color.WHITE);
        DialogUtils.setBackgroundCompat(view, drawable);
    }

    private void initView() {
        positive.setText(R.string.tip_positive);
        negative.setText(R.string.tip_negative);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick({R.id.positive, R.id.negative})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.positive:
                onPositiveClick();
                break;
            case R.id.negative:
                dismiss();
                break;
        }

    }

    /**
     * 响应确定按钮
     */
    private void onPositiveClick() {
        CharSequence titleChar = titleEdt.getText();
        CharSequence contentChar = contentEdt.getText();

        if (Strings.empty(titleChar) && Strings.empty(contentChar)) {
            T.t(R.string.warning_no_content);
            return;
        }

        if (inputCallback != null) {
            inputCallback.onInput(this, titleChar, contentChar);
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public interface InputCallback {

        void onInput(EssayStyleIIAddDialog dialog, CharSequence title, CharSequence content);

    }

}
