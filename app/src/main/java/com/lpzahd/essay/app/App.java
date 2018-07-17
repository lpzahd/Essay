package com.lpzahd.essay.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.lpzahd.atool.constant.Constance;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.T;
import com.lpzahd.derive.container.MiniCup;
import com.lpzahd.essay.exotic.fresco.FrescoInit;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static App app;

    public static App getApp() {
        return app;
    }

    private Handler mHandler;
    private MiniCup<Activity> activityMiniCup = new MiniCup<>();

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        mHandler = new Handler(Looper.getMainLooper());

        registerActivityLifecycleCallbacks(this);
        init(app);
    }

    private void init(Application app) {
        Constance.initApp(app);

        T.init(app);
        Keeper.init(app);
        FrescoInit.get().init(app);
        AndroidThreeTen.init(this);
        CustomActivityOnCrash.install(app);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityMiniCup.putAgain(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityMiniCup.remove(activity);
    }
}
