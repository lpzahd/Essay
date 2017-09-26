package com.lpzahd.essay.context.leisure;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.lpzahd.common.tone.activity.ToneActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.leisure.waiter.LeisureWaiter;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class LeisureActivity extends ToneActivity {

    private LeisureWaiter mLeisureWaiter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LeisureActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(mLeisureWaiter = new LeisureWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_leisure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mLeisureWaiter.onCreateOptionsMenu(menu);
    }
}
