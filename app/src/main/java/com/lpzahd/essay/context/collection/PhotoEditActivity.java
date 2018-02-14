package com.lpzahd.essay.context.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.waiter.PhotoEditWaiter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/13 17:05
 * @change
 */
public class PhotoEditActivity extends RxActivity {

    public static final String TAG = "com.lpzahd.essay.context.collection.PhotoEditWaiter";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PhotoEditActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new PhotoEditWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_photo_edit);
        ButterKnife.bind(this);
        toolBar.setTitle("收藏");
        setSupportActionBar(toolBar);
    }

}
