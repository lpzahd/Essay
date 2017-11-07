package com.lpzahd.essay.context.instinct;

import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.instinct.waiter.YiyiBoxMediaWaiter;

/**
 * 作者 : 迪
 * 时间 : 2017/10/27.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class InstinctMediaActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InstinctMediaActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new YiyiBoxMediaWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_instinct_photo);
    }
}
