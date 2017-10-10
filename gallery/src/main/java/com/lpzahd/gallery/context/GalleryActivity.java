package com.lpzahd.gallery.context;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.waiter.MediaWaiter;
import com.lpzahd.gallery.waiter.MultiSelectWaiter;
import com.lpzahd.waiter.WaiterActivity;
import com.lpzahd.waiter.agency.ActivityWaiter;

/**
 * Author : Lpzahd
 * Date : 七月
 * Desction : (•ิ_•ิ)
 */
public class GalleryActivity extends WaiterActivity {

    private final Handler mHandler = new Handler();

    public Handler getHandler() {
        return mHandler;
    }

    private MediaWaiter mMediaWaiter;

    public MediaWaiter getMediaWaiter() {
        return mMediaWaiter;
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), GalleryActivity.class);
        fragment.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();

        Gallery.Configuration configuration = Gallery.it().getConfig();

        if(!configuration.isReplace()) {
            ActivityWaiter parent;
            @Gallery.Configuration.MODE int mode = configuration.getMode();
            switch (mode) {
                case Gallery.Configuration.MODE_SELECT:
                    parent = new MultiSelectWaiter(this, configuration.getMaxSize());
                    addActivityWaiter(parent);
                    break;
                case Gallery.Configuration.MODE_GALLERY:
                default:
                    addActivityWaiter(mMediaWaiter = new MediaWaiter(this));
                    parent = mMediaWaiter;
                    break;
            }
            for (ActivityWaiter waiter : configuration.getWaiters()) {
                waiter.setContext(this);
                parent.addWaiter(waiter);
            }
        } else {
            for (ActivityWaiter waiter : configuration.getWaiters()) {
                waiter.setContext(this);
                addActivityWaiter(waiter);
            }
        }

    }

    @Override
    protected void inflaterView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Gallery.it().clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
