package com.lpzahd.essay.context.leisure;

import android.content.Context;
import android.content.Intent;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class LeisureActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LeisureActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new LeisureWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_leisure);
    }

}
