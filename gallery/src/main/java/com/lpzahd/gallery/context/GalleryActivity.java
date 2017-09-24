package com.lpzahd.gallery.context;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.presenter.MediaPresenter;
import com.lpzahd.gallery.presenter.MultiSelectPresenter;
import com.lpzahd.waiter.WaiterActivity;
import com.lpzahd.waiter.agency.ActivityWaiter;

import java.util.TimeZone;

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

    private MediaPresenter mMediaPresenter;

    public MediaPresenter getMediaPresenter() {
        return mMediaPresenter;
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();

        Gallery.Configuration configuration = Gallery.it().getConfig();

        if(!configuration.isReplace()) {
            @Gallery.Configuration.MODE int mode = configuration.getMode();
            switch (mode) {
                case Gallery.Configuration.MODE_SELECT:
                    addActivityWaiter(new MultiSelectPresenter(this, 3));
                    break;
                case Gallery.Configuration.MODE_GALLERY:
                default:
                    addActivityWaiter(mMediaPresenter = new MediaPresenter(this));
                    break;
            }
        }

        for (ActivityWaiter waiter : configuration.getWaiters())
            addActivityWaiter(waiter);
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
}
