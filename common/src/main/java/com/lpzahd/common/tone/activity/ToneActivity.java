package com.lpzahd.common.tone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lpzahd.Strings;
import com.lpzahd.waiter.WaiterActivity;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class ToneActivity extends WaiterActivity {

    protected AppCompatActivity context;

    {
        context = this;
    }

    @Override
    protected void inflaterView(@Nullable Bundle savedInstanceState) {
        if (!checkArgus(getIntent())) {
            return;
        }

        onCreate();

        initView();
        initData();

        restoreActivity(savedInstanceState);
    }

    public void onCreate() {

    }

    /**
     * 初始化view
     */
    protected void initView() {

    }

    /**
     * 加载一些数据
     */
    protected void initData() {

    }

    /**
     * activity 恢复临时状态
     *
     * @param savedInstanceState 保存的临时数据
     */
    public void restoreActivity(@Nullable Bundle savedInstanceState) {

    }

    protected boolean checkArgus(Intent intent) {
        return true;
    }

    private MaterialDialog dialog;

    public void showDialog() {
        showDialog(null, null);
    }

    public void showDialog(String title, String content) {
        final MaterialDialog dialog = dialog();

        if (Strings.nonEmpty(title))
            dialog.setTitle(title);

        if (Strings.nonEmpty(content))
            dialog.setTitle(content);

        dialog.show();

    }

    public void dismiss() {
        dialog().dismiss();
    }

    private MaterialDialog dialog() {
        if (dialog == null)
            dialog = new MaterialDialog.Builder(context)
                    .title("之乎者也")
                    .content("waiting...")
                    .canceledOnTouchOutside(false)
                    .progress(true, 0)
                    .build();

        return dialog;
    }



}
