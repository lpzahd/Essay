package com.lpzahd.essay.common.waiter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lpzahd.waiter.agency.ActivityWaiter;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/27 14:07
 * @change
 */
public class OptimizationWaiter extends ActivityWaiter<AppCompatActivity, ActivityWaiter> {

    /**
     * fresco加载图片在滚动时处理
     */
    public static void optimizeFrescoInRecyclerview(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(Fresco.getImagePipeline().isPaused())
                        Fresco.getImagePipeline().resume();
                } else {
                    if(!Fresco.getImagePipeline().isPaused())
                        Fresco.getImagePipeline().pause();
                }
            }
        });
    }
}
