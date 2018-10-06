package com.lpzahd.essay.context.web;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.web.waiter.WebWaiter;

public class WebActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, WebActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new WebWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_web);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Web");
        setSupportActionBar(toolbar);
    }

}
