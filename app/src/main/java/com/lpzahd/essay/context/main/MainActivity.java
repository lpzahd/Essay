package com.lpzahd.essay.context.main;

import android.Manifest;
import android.support.v7.widget.Toolbar;

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
    public void onCreate() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("(＾＿－)");
        setSupportActionBar(toolbar);
    }

}
