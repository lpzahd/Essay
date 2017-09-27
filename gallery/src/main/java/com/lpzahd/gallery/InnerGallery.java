package com.lpzahd.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.waiter.WaiterFragment;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
class InnerGallery {

    protected Gallery.Configuration mConfiguration = new Gallery.Configuration();

    public Gallery.Configuration getConfig() {
        return mConfiguration;
    }

    private void setConfiguration(Gallery.Configuration configuration) {
        mConfiguration = configuration;
    }

    public void clear() {
        mConfiguration.clear();
    }

    public void startActivity(Context context) {
        GalleryActivity.startActivity(context);
    }

    public void startActivity(WaiterFragment fragment) {
        GalleryActivity.startActivity(fragment);
    }

}
