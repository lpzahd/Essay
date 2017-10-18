package com.lpzahd.essay.context.preview;

import android.content.Context;
import android.content.Intent;

import android.view.Window;
import android.view.WindowManager;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.waiter.SinglePicWaiter;

import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class SinglePicActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SinglePicActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new SinglePicWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_single_pic);
    }


}
