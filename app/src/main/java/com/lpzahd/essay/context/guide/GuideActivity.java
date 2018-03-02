package com.lpzahd.essay.context.guide;

import android.Manifest;
import android.content.ComponentName;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.ViewGroup;

import com.lpzahd.common.waiter.permission.PermissionsWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.guide.waiter.GuideStyleIWaiter;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.context.music.client.MediaBrowserAdapter;
import com.lpzahd.essay.context.music.service.MusicService;
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

//    private MediaBrowserAdapter mMediaBrowserAdapter;
//    private boolean isConnected = false;

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_guide_style_01);
        ButterKnife.bind(this);

//        mMediaBrowserAdapter = new MediaBrowserAdapter(this);
//        mMediaBrowserAdapter.addListener(new MediaBrowserAdapter.MediaBrowserChangeListener() {
//            @Override
//            public void onConnected(@Nullable MediaControllerCompat mediaController) {
//                mMediaBrowserAdapter.getTransportControls().play();
//                isConnected = true;
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        mMediaBrowserAdapter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(isConnected) {
//            mMediaBrowserAdapter.getTransportControls().play();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(isConnected) {
//            mMediaBrowserAdapter.getTransportControls().pause();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMediaBrowserAdapter.onStop();
    }
}
