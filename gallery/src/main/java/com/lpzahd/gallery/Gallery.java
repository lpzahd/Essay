package com.lpzahd.gallery;

import android.content.Context;
import android.support.annotation.IntDef;

import com.lpzahd.base.NoInstance;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.waiter.agency.ActivityWaiter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 */
public class Gallery {

    private static Gallery gallery = new Gallery();

    private Gallery() {
    }

    public static Gallery it() {
        return gallery;
    }

    private Configuration mConfiguration = new Configuration();

    public Configuration getConfig() {
        return mConfiguration;
    }

    public void setConfiguration(Configuration configuration) {
        mConfiguration = configuration;
    }

    public void clear() {
        mConfiguration.clear();
    }

    public void startActivity(Context context) {
        GalleryActivity.startActivity(context);
    }

    public static class Configuration {

        public static final int MODE_GALLERY = 0;
        public static final int MODE_SELECT = 1;

        @IntDef({MODE_GALLERY, MODE_SELECT})
        @Retention(RetentionPolicy.SOURCE)
        public @interface MODE {}

        private Configuration(){}

        private List<ActivityWaiter> waiters;
        private boolean replace = false;

        private @MODE int mode = MODE_GALLERY;

        public List<ActivityWaiter> getWaiters() {
            return waiters != null ? waiters : (waiters = new ArrayList<>());
        }

        public void setWaiters(List<ActivityWaiter> waiters) {
            this.waiters = waiters;
        }

        public void addWaiter(ActivityWaiter waiter) {
            getWaiters().add(waiter);
        }

        public boolean isReplace() {
            return replace;
        }

        public void setReplace(boolean replace) {
            this.replace = replace;
        }

        public @MODE int getMode() {
            return mode;
        }

        public void setMode(@MODE int mode) {
            this.mode = mode;
        }

        void clear() {
            if (waiters != null)
                waiters.clear();
        }
    }

}
