package com.lpzahd.common.tone.waiter;

import android.content.Context;
import android.view.View;

import com.lpzahd.waiter.agency.WindowWaiter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class ToneWindowWaiter extends WindowWaiter {

    protected Context context;

    private Unbinder unbinder;

    @Override
    protected void create(View rootView) {
        super.create(rootView);

        context = rootView.getContext();
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    protected void destroy() {
        super.destroy();
        unbinder.unbind();
    }
}
