package com.lpzahd.essay.context.preview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class PreviewPicActivity extends RxActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PreviewPicActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new PreviewPicWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_preview_pic);
        ButterKnife.bind(this);

        toolBar.setTitle("预览");
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

}
