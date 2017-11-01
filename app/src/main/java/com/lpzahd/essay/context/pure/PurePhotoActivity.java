package com.lpzahd.essay.context.pure;

import android.content.Context;
import android.content.Intent;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.pure.waiter.PurePhotoWaiter;

/**
 * 作者 : 迪
 * 时间 : 2017/11/1.
 * 描述 ： 命里有时终须有，命里无时莫强求
 * 清纯唯美图片册
 */
public class PurePhotoActivity extends RxActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PurePhotoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new PurePhotoWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_pure_photo);
    }

}
