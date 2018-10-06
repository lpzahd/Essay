package com.lpzahd.essay.common.context;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.lpzahd.atool.action.Check;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.common.tone.waiter.WaiterManager;
import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.agency.WindowWaiter;
import com.lpzahd.waiter.waiter.Waiter;

public class MediaSelectActivity extends RxActivity {

    public static final String TAG = "com.lpzahd.essay.common.context.MediaSelectActivity";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MediaSelectActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();

        Waiter[] waiters = WaiterManager.single().get(MediaSelectActivity.class);

        if(Check.Empty.check(waiters, () -> T.t("无参"))) return;

        for(Waiter waiter : waiters) {
            if(waiter instanceof ActivityWaiter) {
                ActivityWaiter<MediaSelectActivity, ActivityWaiter> activityWaiter = (ActivityWaiter<MediaSelectActivity, ActivityWaiter>) waiter;
                activityWaiter.setContext(this);
                addActivityWaiter(activityWaiter);
            } else if(waiter instanceof WindowWaiter) {
                WindowWaiter windowWaiter = (WindowWaiter) waiter;
                addWindowWaiter(windowWaiter);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WaiterManager.single().remove(MediaSelectActivity.class);
    }
}