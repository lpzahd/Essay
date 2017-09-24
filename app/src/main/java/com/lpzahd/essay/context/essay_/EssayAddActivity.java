package com.lpzahd.essay.context.essay_;

import android.content.Context;
import android.content.Intent;

import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.waiter.EssayAddWaiter;
import com.lpzahd.common.tone.activity.RxActivity;

import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 * <p>
 * 点击图片切换的时候可以加转场效果
 */
public class EssayAddActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EssayAddActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new EssayAddWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_essay_add);
        ButterKnife.bind(this);
    }

}
