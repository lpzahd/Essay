package com.lpzahd.essay.context.essay;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.lpzahd.atool.ui.T;
import com.lpzahd.circualreveal.animation.ViewAnimationUtils;
import com.lpzahd.essay.R;
import com.lpzahd.common.tone.fragment.ToneDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class EssayEditDialog extends ToneDialogFragment {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CONTENT = "extra_content";

    @BindView(R.id.title_input_layout)
    TextInputLayout titleInputLayout;
    @BindView(R.id.content_input_layout)
    TextInputLayout contentInputLayout;
    @BindView(R.id.positive)
    TextView positive;
    @BindView(R.id.negative)
    TextView negative;
    @BindView(R.id.essay_layout)
    LinearLayout essayLayout;

    EditText title;
    EditText content;

    private CharSequence titleChar = "";
    private CharSequence contentChar = "";

    // 是否是编辑状态
    private boolean isEdit;

    // 点击是否关闭
    private boolean isAutoDismiss;

    InputCallback inputCallback;

    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }

    public static EssayEditDialog newEssaylDialog(CharSequence title, CharSequence content) {
        EssayEditDialog dialog = new EssayEditDialog();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_TITLE, title);
        bundle.putCharSequence(EXTRA_CONTENT, content);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            titleChar = bundle.getCharSequence(EXTRA_TITLE, "");
            contentChar = bundle.getCharSequence(EXTRA_CONTENT, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = inflater.inflate(R.layout.dialog_essay_edit, container, false);
        ButterKnife.bind(this, rootView);

        title = titleInputLayout.getEditText();
        content = contentInputLayout.getEditText();

        setBackground(essayLayout);
        initView();

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAutoDismiss)
                    dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 配置圆角背景
     * @param view 目标view
     */
    private void setBackground(View view) {
        final Context context = getContext();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(context.getResources().getDimension(R.dimen.corner_radius_d4));
        drawable.setColor(Color.WHITE);
        DialogUtils.setBackgroundCompat(view, drawable);
    }

    private void initView() {
        showOrEdit();
    }

    private void showOrEdit() {
        if(isEdit) {
            showEdit();
        } else {
            showContent();
        }
    }

    private void showContent() {
        // 失去焦点
        essayLayout.requestFocus();

        getDialog().setCanceledOnTouchOutside(true);
        isAutoDismiss = true;

        title.setFocusableInTouchMode(false);
        content.setFocusableInTouchMode(false);

        final int color = ContextCompat.getColor(getContext(), R.color.color_txt_edit_disable);
        title.setTextColor(color);
        content.setTextColor(color);

        title.setText(titleChar);
        content.setText(contentChar);

        if(negative.getVisibility() != View.GONE)
            negative.setVisibility(View.GONE);

        positive.setText("编辑");
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                showOrEdit();

                anim();
            }
        });
    }

    private void showEdit() {
        getDialog().setCanceledOnTouchOutside(false);
        isAutoDismiss = false;

        title.setFocusableInTouchMode(true);
        content.setFocusableInTouchMode(true);

        final int color = ContextCompat.getColor(getContext(), R.color.color_txt_edit_enable);
        title.setTextColor(color);
        content.setTextColor(color);

        title.setSelection(title.getText().length());
        title.requestFocus();

        if(negative.getVisibility() != View.VISIBLE)
            negative.setVisibility(View.VISIBLE);

        positive.setText(R.string.tip_positive);
        negative.setText(R.string.tip_negative);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPositiveClick();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                showOrEdit();

                anim();
            }
        });
    }

    /**
     * 响应确定按钮
     */
    private void onPositiveClick() {
        CharSequence titleChar = title.getText();
        CharSequence contentChar = content.getText();

        // 判null
        if(titleChar.toString().isEmpty() && contentChar.toString().isEmpty()) {
            T.t(R.string.warning_no_content);
            return ;
        }

        if (inputCallback != null) {
            inputCallback.onInput(this, titleChar, contentChar);
        }
        dismiss();
    }

    private void anim() {
        // get the center for the clipping circle
        int cx = essayLayout.getWidth();
        int cy = essayLayout.getHeight();

        // 斜边
        float finalRadius = (float) Math.hypot(cx, cy);

        // Android native animator
        Animator animator =
                ViewAnimationUtils.createCircularReveal(essayLayout, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(800);
        animator.start();

    }

    public interface InputCallback {

        void onInput(EssayEditDialog dialog, CharSequence title, CharSequence content);

    }

}
