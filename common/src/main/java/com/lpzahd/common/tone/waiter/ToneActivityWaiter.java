package com.lpzahd.common.tone.waiter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.waiter.AwcWaiter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public abstract class ToneActivityWaiter<E extends AppCompatActivity> extends AwcWaiter<E, ActivityWaiter> {

    private Unbinder unbinder;

    protected View rootView;

    public ToneActivityWaiter() {super();}

    public ToneActivityWaiter(E e) {
        super(e);
    }

    public ToneActivityWaiter(int gradation, E context) {
        super(gradation, context);
    }

    @SuppressWarnings("unchecked")
    protected <V extends View> V getRootView() {
        if(rootView == null) {
            rootView = context.getWindow().getDecorView();
        }

        return (V) rootView;
    }

    protected <V extends View> void setRootView(V v) {
        rootView = v;
    }

    @Override
    final protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        setContentView();
        if (!checkArgus(context.getIntent())) {
            return;
        }

        unbinder = ButterKnife.bind(this, getRootView());

        initToolBar();
        initView();
        initData();

        restoreState(savedInstanceState);
    }

    @Override
    protected void destroy() {
        super.destroy();
        unbinder.unbind();
    }

    protected void setContentView() {

    }

    protected void initToolBar() {

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
    public void restoreState(@Nullable Bundle savedInstanceState) {

    }

    protected boolean checkArgus(Intent intent) {
        return true;
    }

}
