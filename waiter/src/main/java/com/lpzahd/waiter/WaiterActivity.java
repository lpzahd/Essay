package com.lpzahd.waiter;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.agency.WindowWaiter;
import com.lpzahd.waiter.consumer.State;

/**
 * Author : Lpzahd
 * Date : 16:50
 * Desction : (•ิ_•ิ)
 */
public abstract class WaiterActivity extends AppCompatActivity {

    private WindowWaiter<WindowWaiter> windowWaiter = new WindowWaiter<>();

    public WindowWaiter<WindowWaiter> getWindowWaiter() {
        return windowWaiter;
    }

    private ActivityWaiter<AppCompatActivity, ActivityWaiter> activityWaiter = new ActivityWaiter<AppCompatActivity, ActivityWaiter>(this);

    public ActivityWaiter getActivityWaiter() {
        return activityWaiter;
    }

    public void addWindowWaiter(WindowWaiter windowWaiter) {
        this.windowWaiter.addWaiter(windowWaiter);
    }

    public void addActivityWaiter(ActivityWaiter activityWaiter) {
        this.activityWaiter.addWaiter(activityWaiter);
    }

    public void init() {
    }

    /**
     * repalce onCreate(Bundle savedInstanceState)
     */
    protected abstract void inflaterView(@Nullable Bundle savedInstanceState);

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        inflaterView(savedInstanceState);
        activityWaiter.onCreate(savedInstanceState);
        windowWaiter.onCreate(getWindow().getDecorView());
    }

    @Override
    public void onStart() {
        super.onStart();
        activityWaiter.onStart();
        windowWaiter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityWaiter.onResume();
        windowWaiter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        activityWaiter.onPause();
        windowWaiter.onPause();
    }

    @Override
    public void onStop() {
        activityWaiter.onStop();
        windowWaiter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        activityWaiter.onDestroy();
        windowWaiter.onDestroy();
        super.onDestroy();

        activityWaiter.clear();
        windowWaiter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityWaiter.onSaveInstanceState(outState);
        windowWaiter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        activityWaiter.onRestoreInstanceState(savedInstanceState);
        windowWaiter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        activityWaiter.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        activityWaiter.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activityWaiter.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        activityWaiter.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityWaiter.onActivityResult(requestCode, resultCode, data);
        windowWaiter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        activityWaiter.onRequestPermissionsResult(requestCode, permissions, grantResults);
        windowWaiter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        if(activityWaiter.finish() == State.STATE_IGNORE)
            super.finish();
    }

    @Override
    public void onBackPressed() {
        if(activityWaiter.onBackPressed() == State.STATE_IGNORE)
            super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int state = activityWaiter.onKeyDown(keyCode, event);
        if(state == State.STATE_IGNORE)
            return super.onKeyDown(keyCode, event);
        else
            return state != State.STATE_PREVENT;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        activityWaiter.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        activityWaiter.onLowMemory();
    }
}
