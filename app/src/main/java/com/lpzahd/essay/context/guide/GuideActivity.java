package com.lpzahd.essay.context.guide;

import android.Manifest;
import android.view.ViewGroup;

import com.lpzahd.common.waiter.permission.PermissionsWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.guide.waiter.GuideStyleIWaiter;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.exotic.realm.Realmer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideActivity extends RxActivity {

    @BindView(R.id.activity_guide)
    ViewGroup activityGuide;

    PermissionsWaiter mPermissionsWaiter;

    @Override
    public void init() {
        super.init();
//        addActivityWaiter(new RecyclerWaiter(this));
//        addActivityWaiter(new GuideWaiter(this));
        addActivityWaiter(mPermissionsWaiter = new PermissionsWaiter(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE));

        mPermissionsWaiter.setAccept(new PermissionsWaiter.Accept() {
            @Override
            public void accept() {
                Realmer.init();
            }
        });

        addActivityWaiter(new GuideStyleIWaiter(this));
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_guide_style_01);
        ButterKnife.bind(this);
        showKangNaDialog();
    }


}
