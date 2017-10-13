package com.lpzahd.essay.context.turing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.turing.waiter.TuringWaiter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TuringActivity extends RxActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TuringActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new TuringWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_turing);
        ButterKnife.bind(this);
        toolBar.setTitle("随笔");
        setSupportActionBar(toolBar);
    }

}
