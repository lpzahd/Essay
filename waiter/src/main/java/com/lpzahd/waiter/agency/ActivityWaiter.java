package com.lpzahd.waiter.agency;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.lpzahd.waiter.consumer.IActivity;
import com.lpzahd.waiter.consumer.State;
import com.lpzahd.waiter.waiter.Waiter;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class ActivityWaiter<E extends AppCompatActivity, T extends ActivityWaiter> extends Waiter<T> implements IActivity {

    protected E context;

    public ActivityWaiter() {}

    public ActivityWaiter(E e) {
        context = e;
    }

    public ActivityWaiter(int gradation, E context) {
        super(gradation);
        this.context = context;
    }

    final public void setContext(E context) {
        this.context = context;
    }

    final public E getContext() {
        return context;
    }

    protected void init() {
    }

    protected void create(Bundle savedInstanceState) {
    }

    protected void start() {
    }

    protected void resume() {
    }

    protected void pause() {
    }

    protected void stop() {
    }

    protected void destroy() {
    }

    protected void saveState(Bundle outState) {
    }

    protected void restoreState(Bundle savedInstanceState) {
    }

    protected void attachedToWindow() {
    }

    protected void detachedFromWindow() {
    }

    protected void restart() {
    }

    protected void newIntent(Intent intent) {
    }

    protected void activityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    protected @State.InnerState int onFinish() {
        return State.STATE_IGNORE;
    }

    protected @State.InnerState int backPressed() {
        return State.STATE_IGNORE;
    }

    protected @State.InnerState int keyDown(int keyCode, KeyEvent event) {
        return State.STATE_IGNORE;
    }

    protected void configurationChanged(Configuration newConfig) {

    }

    protected void lowMemory() {

    }


    protected @State.InnerState int createOptionsMenu(Menu menu) {
        return State.STATE_IGNORE;
    }

    protected @State.InnerState int optionsItemSelected(MenuItem item) {
        return State.STATE_IGNORE;
    }

    @Override
    final public void onCreate(Bundle savedInstanceState) {
        init();
        create(savedInstanceState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onCreate(savedInstanceState);
        }
    }

    @Override
    final public void onStart() {
        start();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onStart();
        }
    }

    @Override
    final public void onResume() {
        resume();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onResume();
        }
    }

    @Override
    final public void onPause() {
        pause();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onPause();
        }
    }

    @Override
    final public void onStop() {
        stop();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onStop();
        }
    }

    @Override
    final public void onDestroy() {
        destroy();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onDestroy();
        }
    }

    @Override
    final public void onAttachedToWindow() {
        attachedToWindow();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onAttachedToWindow();
        }
    }

    @Override
    final public void onDetachedFromWindow() {
        detachedFromWindow();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onDetachedFromWindow();
        }
    }

    @Override
    final public void onRestart() {
        restart();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onRestart();
        }
    }

    @Override
    final public void onNewIntent(Intent intent) {
        newIntent(intent);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onNewIntent(intent);
        }
    }

    @Override
    final public void onSaveInstanceState(Bundle outState) {
        saveState(outState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onSaveInstanceState(outState);
        }
    }

    @Override
    final public void onRestoreInstanceState(Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    final public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResult(requestCode, resultCode, data);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    final public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        requestPermissionsResult(requestCode, permissions, grantResults);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    final public @State.InnerState int finish() {
        @State.InnerState int state = onFinish();
        if(state != State.STATE_IGNORE) {
            return state;
        }

        for (int i = 0, size = waiterTeam.size(); i < size; i++) {
            @State.InnerState int teamState = waiterTeam.get(i).finish();
            if(teamState != State.STATE_IGNORE) {
                return teamState;
            }
        }
        return State.STATE_IGNORE;
    }

    @Override
    final public @State.InnerState int onBackPressed() {
        @State.InnerState int state = backPressed();
        if(state != State.STATE_IGNORE) {
            return state;
        }

        for (int i = 0, size = waiterTeam.size(); i < size; i++) {
            @State.InnerState int teamState = waiterTeam.get(i).onBackPressed();
            if(teamState != State.STATE_IGNORE) {
                return teamState;
            }
        }
        return State.STATE_IGNORE;
    }

    @Override
    final public @State.InnerState int onKeyDown(int keyCode, KeyEvent event) {
        @State.InnerState int state = keyDown(keyCode, event);
        if(state != State.STATE_IGNORE) {
            return state;
        }

        for (int i = 0, size = waiterTeam.size(); i < size; i++) {
            @State.InnerState int teamState = waiterTeam.get(i).onKeyDown(keyCode, event);
            if(teamState != State.STATE_IGNORE) {
                return teamState;
            }
        }
        return State.STATE_IGNORE;
    }

    @Override
    final public void onConfigurationChanged(Configuration newConfig) {
        configurationChanged(newConfig);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onConfigurationChanged(newConfig);
        }
    }

    @Override
    final public void onLowMemory() {
        lowMemory();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onLowMemory();
        }
    }

    @Override
    final public int onCreateOptionsMenu(Menu menu) {
        @State.InnerState int state = createOptionsMenu(menu);
        if(state != State.STATE_IGNORE) {
            return state;
        }

        for (int i = 0, size = waiterTeam.size(); i < size; i++) {
            @State.InnerState int teamState = waiterTeam.get(i).onCreateOptionsMenu(menu);
            if(teamState != State.STATE_IGNORE) {
                return teamState;
            }
        }
        return State.STATE_IGNORE;
    }


    @Override
    final public int onOptionsItemSelected(MenuItem item) {
        @State.InnerState int state = optionsItemSelected(item);
        if(state != State.STATE_IGNORE) {
            return state;
        }

        for (int i = 0, size = waiterTeam.size(); i < size; i++) {
            @State.InnerState int teamState = waiterTeam.get(i).onOptionsItemSelected(item);
            if(teamState != State.STATE_IGNORE) {
                return teamState;
            }
        }
        return State.STATE_IGNORE;
    }


}
