package com.lpzahd.waiter.agency;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lpzahd.waiter.consumer.IFragment;
import com.lpzahd.waiter.waiter.Waiter;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public class FragmentWaiter<E extends Fragment, T extends FragmentWaiter> extends Waiter<T> implements IFragment {

    protected E fragment;

    public FragmentWaiter(E fragment) {
        this.fragment = fragment;
    }

    public FragmentWaiter(int gradation, E fragment) {
        super(gradation);
        this.fragment = fragment;
    }

    public <A extends AppCompatActivity> A getActivity() {
        return (A) fragment.getActivity();
    }

    protected void init() {
    }

    protected void attach(Context context) {
    }

    protected void create(Bundle savedInstanceState) {
    }

    protected void createView(View rootView) {
    }

    protected void activityCreated(Bundle savedInstanceState) {
    }

    protected void start() {
    }

    protected void resume() {
    }

    protected void pause() {
    }

    protected void stop() {
    }

    protected void destroyView() {
    }

    protected void destroy() {
    }

    protected void detach() {
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
    final public void onAttach(Context context) {
        waiterTeam.clear();
        init();
        attach(context);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onAttach(context);
        }
    }

    @Override
    final public void onCreate(Bundle savedInstanceState) {
        create(savedInstanceState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onCreate(savedInstanceState);
        }
    }

    @Override
    final public void onCreateView(View rootView) {
        createView(rootView);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onCreateView(rootView);
        }
    }

    @Override
    final public void onActivityCreated(Bundle savedInstanceState) {
        activityCreated(savedInstanceState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onActivityCreated(savedInstanceState);
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
    final public void onDestroyView() {
        destroyView();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onDestroyView();
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
    final public void onDetach() {
        detach();
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onDetach();
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
    final public void onViewStateRestored(Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onViewStateRestored(savedInstanceState);
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
    final public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        requestPermissionsResult(requestCode, permissions, grantResults);
        int size = waiterTeam.size();
        for (int i = 0; i < size; i++) {
            waiterTeam.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
