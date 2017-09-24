package com.lpzahd.waiter.waiter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.agency.WindowWaiter;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ) activity/window 复合使者
 */
public class AwcWaiter<E extends AppCompatActivity, T extends ActivityWaiter> extends ActivityWaiter<E, T> {

    private WindowWaiter<WindowWaiter> windowWaiter = new WindowWaiter<>();

    public WindowWaiter<WindowWaiter> getWindowWaiter() {
        return windowWaiter;
    }

    public void addWindowWaiter(WindowWaiter windowWaiter) {
        this.windowWaiter.addWaiter(windowWaiter);
    }

    public AwcWaiter(E e) {
        super(e);
    }

    public AwcWaiter(int gradation, E context) {
        super(gradation, context);
    }

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        windowWaiter.onCreate(context.getWindow().getDecorView());
    }

    @Override
    protected void start() {
        super.start();
        windowWaiter.onStart();
    }

    @Override
    protected void resume() {
        super.resume();
        windowWaiter.onResume();
    }

    @Override
    protected void pause() {
        windowWaiter.onPause();
        super.pause();
    }

    @Override
    protected void stop() {
        windowWaiter.onStop();
        super.stop();
    }

    @Override
    protected void destroy() {
        windowWaiter.onDestroy();
        super.destroy();
        windowWaiter.clear();
    }

    @Override
    protected void saveState(Bundle outState) {
        super.saveState(outState);
        windowWaiter.onSaveInstanceState(outState);
    }

    @Override
    protected void restoreState(Bundle savedInstanceState) {
        super.restoreState(savedInstanceState);
        windowWaiter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data) {
        super.activityResult(requestCode, resultCode, data);
        windowWaiter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.requestPermissionsResult(requestCode, permissions, grantResults);
        windowWaiter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

