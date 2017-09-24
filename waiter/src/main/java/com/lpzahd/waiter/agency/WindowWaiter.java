package com.lpzahd.waiter.agency;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lpzahd.waiter.consumer.IWindow;
import com.lpzahd.waiter.waiter.Waiter;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class WindowWaiter<T extends WindowWaiter> extends Waiter<T> implements IWindow {

    public WindowWaiter() {
    }

    public WindowWaiter(int gradation) {
        super(gradation);
    }

    protected void init() {
    }

    protected void create(View rootView) {
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

    protected void activityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }


    @Override
    final public void onCreate(View rootView) {
        init();
        create(rootView);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onCreate(rootView);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResult(requestCode, resultCode, data);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        requestPermissionsResult(requestCode, permissions, grantResults);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
