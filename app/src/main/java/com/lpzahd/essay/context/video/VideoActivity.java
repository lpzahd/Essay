package com.lpzahd.essay.context.video;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.turing.TuringActivity;
import com.lpzahd.essay.context.video.waiter.VideoWaiter;

/**
 * @author lpzahd
 * @describe
 * @time 2018/3/12 16:54
 * @change
 */
public class VideoActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new VideoWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_video);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("视频");
        setSupportActionBar(toolbar);
    }
}
