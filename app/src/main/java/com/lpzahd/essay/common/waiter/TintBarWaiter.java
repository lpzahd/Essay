package com.lpzahd.essay.common.waiter;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.lpzahd.essay.R;
import com.lpzahd.waiter.agency.ActivityWaiter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class TintBarWaiter extends ActivityWaiter<AppCompatActivity, ActivityWaiter>{

    public TintBarWaiter(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    public TintBarWaiter(int gradation, AppCompatActivity context) {
        super(gradation, context);
    }

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(false);
        }
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);

        setTintResource(tintManager);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void setTintResource(SystemBarTintManager manager) {
        manager.setTintResource(R.color.colorPrimaryDark);
    }
}
