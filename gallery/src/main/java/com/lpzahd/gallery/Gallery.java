package com.lpzahd.gallery;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.gallery.presenter.MultiSelectPresenter;
import com.lpzahd.waiter.LifecleCallBack;
import com.lpzahd.waiter.WaiterActivity;
import com.lpzahd.waiter.WaiterFragment;
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
public class Gallery extends InnerGallery {

    private static Gallery sGallery = new Gallery();

    private Gallery() {
    }

    public static Gallery it() {
        return sGallery;
    }

    public static <T extends WaiterActivity> Gallery with(T context) {
        sGallery = new Gallery();
        sGallery.mConfiguration.setContext(context);
        return sGallery;
    }

    public static <T extends WaiterFragment> Gallery with(T context) {
        sGallery = new Gallery();
        sGallery.mConfiguration.setContext(context);
        return sGallery;
    }

    public ImageGallery image() {
        sGallery.mConfiguration.setMode(Configuration.MODE_SELECT);
        return new ImageGallery(sGallery.mConfiguration);
    }

    public interface ActionWaiter<T extends ActivityWaiter> {
         void action(T waiter);
    }

    public static class ImageGallery extends InnerGallery {

        private ImageGallery(Configuration configuration) {
            mConfiguration = configuration;
        }

        public void setConfiguration(Configuration configuration) {
            mConfiguration = configuration;
        }

        public ImageGallery action(ActionWaiter<MultiSelectPresenter> action) {
            mConfiguration.setAction(action);
            return this;
        }

        public ImageGallery maxSize(@IntRange(from = 1) int maxSize) {
            mConfiguration.setMaxSize(maxSize);
            return this;
        }

        public ImageGallery subscribe(@NonNull Receiver receiver) {
            mConfiguration.setBusService(new RxBus.BusService(this, receiver));
            if(Configuration.isActivity(mConfiguration)) {
                mConfiguration.activity.addLifecleCallBack(new SimpleLifecleCallBack<WaiterActivity>() {
                    @Override
                    public void created(WaiterActivity activity) {
                        mConfiguration.getBusService().regist();
                    }

                    @Override
                    public void destroyed(WaiterActivity activity) {
                        mConfiguration.getBusService().regist();
                    }
                });
            } else {
                mConfiguration.fragment.addLifecleCallBack(new SimpleLifecleCallBack<WaiterFragment>() {
                    @Override
                    public void created(WaiterFragment fragment) {
                        mConfiguration.getBusService().regist();
                    }

                    @Override
                    public void destroyed(WaiterFragment fragment) {
                        mConfiguration.getBusService().regist();
                    }
                });
            }
            return this;
        }

        public void openGallery() {
            if(Configuration.isActivity(mConfiguration)) {
                startActivity(mConfiguration.activity);
            } else {
                startActivity(mConfiguration.fragment);
            }
        }

    }

    public static class Configuration {

        public static final int MODE_GALLERY = 0;
        public static final int MODE_SELECT = 1;

        @IntDef({MODE_GALLERY, MODE_SELECT})
        @Retention(RetentionPolicy.SOURCE)
        public @interface MODE {}

        public Configuration(){}

        private List<ActivityWaiter> waiters;
        private boolean replace = false;

        private @MODE int mode = MODE_GALLERY;

        private WaiterActivity activity;
        private WaiterFragment fragment;

        private @IntRange(from = 1) int maxSize = 1;

        private RxBus.BusService busService;

        private ActionWaiter action;

        public List<ActivityWaiter> getWaiters() {
            return waiters != null ? waiters : (waiters = new ArrayList<>());
        }

        public void setContext(WaiterActivity activity) {
            this.activity = activity;
            this.fragment = null;
        }

        public void setContext(WaiterFragment fragment) {
            this.fragment = fragment;
            this.activity = null;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public RxBus.BusService getBusService() {
            return busService;
        }

        public void setBusService(RxBus.BusService busService) {
            this.busService = busService;
        }

        public ActionWaiter getAction() {
            return action;
        }

        public void setAction(ActionWaiter action) {
            this.action = action;
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

        public static boolean isActivity(Configuration configuration) {
            return configuration.activity != null;
        }
    }

    public static class SimpleLifecleCallBack<T> implements LifecleCallBack<T> {

        @Override
        public void created(T t) {

        }

        @Override
        public void started(T t) {

        }

        @Override
        public void resumed(T t) {

        }

        @Override
        public void paused(T t) {

        }

        @Override
        public void stopped(T t) {

        }

        @Override
        public void destroyed(T t) {

        }

        @Override
        public void saveInstanceState(T t) {

        }
    }
}
