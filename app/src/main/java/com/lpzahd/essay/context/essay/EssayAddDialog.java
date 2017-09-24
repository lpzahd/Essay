package com.lpzahd.essay.context.essay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.lpzahd.Strings;
import com.lpzahd.atool.ui.T;
import com.lpzahd.essay.R;
import com.lpzahd.common.tone.fragment.ToneDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 迪 on 2016/9/17.
 */

public class EssayAddDialog extends ToneDialogFragment {

    @BindView(R.id.title_input_layout)
    TextInputLayout titleInputLayout;

    @BindView(R.id.content_input_layout)
    TextInputLayout contentInputLayout;

    @BindView(R.id.positive)
    AppCompatTextView positive;

    @BindView(R.id.negative)
    AppCompatTextView negative;

    @BindView(R.id.essay_parent)
    LinearLayout essayParent;

    EditText title;
    EditText content;

    InputCallback inputCallback;

    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        View rootView = inflater.inflate(R.layout.dialog_essay_add, container, false);
        ButterKnife.bind(this, rootView);

        title = titleInputLayout.getEditText();
        content = contentInputLayout.getEditText();

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
        CharSequence titleChar = title.getText();
        CharSequence contentChar = content.getText();

        if (Strings.empty(titleChar) && Strings.empty(contentChar)) {
            T.t(R.string.warning_no_content);
            return;
        }

        if (inputCallback != null) {
            inputCallback.onInput(this, titleChar, contentChar);
        }
        dismiss();
    }

    public interface InputCallback {

        void onInput(EssayAddDialog dialog, CharSequence title, CharSequence content);

    }

}
