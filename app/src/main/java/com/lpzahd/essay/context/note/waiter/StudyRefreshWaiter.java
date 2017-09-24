package com.lpzahd.essay.context.note.waiter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.lpzahd.essay.R;
import com.lpzahd.common.tone.waiter.ToneWindowWaiter;

import butterknife.BindView;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class StudyRefreshWaiter extends ToneWindowWaiter {

    @BindView(R.id.abnormal_view_stub)
    ViewStub abnormalViewStub;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void create(View rootView) {
        super.create(rootView);
    }

}
