package com.lpzahd.essay.context.instinct;

import android.content.Context;
import android.content.Intent;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.instinct.waiter.YiyiBoxWaiter;

/**
 * 作者 : 迪
 * 时间 : 2017/10/27.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class InstinctActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InstinctActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new YiyiBoxWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_instinct);
    }
}
