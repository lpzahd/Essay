package com.lpzahd.essay.context.main;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.common.waiter.permission.PermissionsWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.main.waiter.GuideNavigationWaiter;
import com.lpzahd.essay.context.main.waiter.GuideRecyclerWaiter;
import com.lpzahd.essay.exotic.realm.Realmer;

public class MainActivity extends RxActivity {

    @Override
    public void init() {
        super.init();
        PermissionsWaiter permissionsWaiter = new PermissionsWaiter(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsWaiter.setAccept(new PermissionsWaiter.Accept() {
            @Override
            public void accept() {
                Realmer.init();
            }
        });
        addActivityWaiter(permissionsWaiter);

        addActivityWaiter(new GuideNavigationWaiter(this));
        addActivityWaiter(new GuideRecyclerWaiter(this));

    }

    @Override
    protected void setTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Essay");
        setSupportActionBar(toolbar);
    }

}
