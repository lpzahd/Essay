package com.lpzahd.essay.context.note.waiter;

import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lpzahd.animview.AnimView;
import com.lpzahd.essay.R;
import com.lpzahd.common.tone.waiter.ToneWindowWaiter;

import butterknife.BindView;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class StudySearchWaiter extends ToneWindowWaiter {

    @BindView(R.id.search_anim_view)
    AnimView searchAnimView;

    @BindView(R.id.search_btn)
    AppCompatButton searchBtn;

    @BindView(R.id.search_tag_recycler_view)
    RecyclerView searchTagRecyclerView;

    @Override
    protected void create(View rootView) {
        super.create(rootView);

        initViews();
    }

    private void initViews() {
        searchAnimView.setAnimListener(new AnimView.AnimListener() {
            @Override
            public void onAnimStart(AnimView view, int state) {
                Log.e("hit", "onAnimStart");
                if(state == AnimView.STATE_ANIM_START) {
                    return;
                }

                searchBtn.setVisibility(View.VISIBLE);
                searchTagRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimRunning(AnimView view, int state, float pro) {
                searchBtn.setScaleX(pro);
                searchBtn.setScaleY(pro);
                searchBtn.setAlpha(pro);

                searchTagRecyclerView.setScaleX(pro);
                searchTagRecyclerView.setAlpha(pro);
            }

            @Override
            public void onAnimEnd(AnimView view, int state) {
                Log.e("hit", "onAnimEnd");
                if(state == AnimView.STATE_ANIM_NONE) {
                    searchBtn.setVisibility(View.INVISIBLE);
                    searchTagRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void resume() {
        super.resume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            searchAnimView.resume();
        }
    }

    @Override
    protected void pause() {
        super.pause();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            searchAnimView.pause();
        }
    }
}
