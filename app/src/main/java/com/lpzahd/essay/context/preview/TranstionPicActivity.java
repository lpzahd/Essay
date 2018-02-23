package com.lpzahd.essay.context.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.waiter.TranstionPicWaiter;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/23 16:00
 * @change
 */
public class TranstionPicActivity extends RxActivity {

    public static final String TAG = "com.lpzahd.essay.context.preview.TranstionPicActivity";

    public static final String SHARE_ELEMENT_NAME = "share_pic";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TranstionPicActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Activity activity, View share) {
        Intent intent = new Intent(activity, TranstionPicActivity.class);
        ViewCompat.setTransitionName(share, SHARE_ELEMENT_NAME);
        ActivityOptionsCompat opt = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                share, SHARE_ELEMENT_NAME);
        ActivityCompat.startActivity(activity, intent, opt.toBundle());
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new TranstionPicWaiter(this));
    }

    @Override
    public void onCreate() {
        // 黑屏
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        super.onCreate();
        setContentView(R.layout.activity_transtion_pic);
    }

}
