package com.lpzahd.essay.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.lpzahd.atool.constant.Constance;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.T;
import com.lpzahd.derive.container.MiniCup;
import com.lpzahd.essay.exotic.fresco.FrescoInit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    private MiniCup<Activity> activityMiniCup;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        Constance.initApp(app);
        T.init(app);
        Keeper.init(app);

        activityMiniCup = new MiniCup<>();
        registerActivityLifecycleCallbacks(this);

        FrescoInit.get().init(app);

        AndroidThreeTen.init(this);
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
