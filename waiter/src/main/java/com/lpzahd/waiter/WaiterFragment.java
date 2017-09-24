package com.lpzahd.waiter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lpzahd.waiter.agency.FragmentWaiter;
import com.lpzahd.waiter.agency.WindowWaiter;

/**
 * Author : Lpzahd
 * Date : 16:51
 * Desction : (•ิ_•ิ)
 */
public abstract class WaiterFragment extends Fragment {

    private WindowWaiter windowWaiter = new WindowWaiter();

    public WindowWaiter getWindowWaiter() {
        return windowWaiter;
    }

    private FragmentWaiter fragmentWaiter = new FragmentWaiter(this);

    public FragmentWaiter getFragmentWaiter() {
        return fragmentWaiter;
    }

    public void init() {

    }

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Activity activity) {
        fragmentWaiter.clear();
        windowWaiter.clear();
        init();
        super.onAttach(activity);
        fragmentWaiter.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        fragmentWaiter.clear();
        windowWaiter.clear();
        init();
        super.onAttach(context);
        fragmentWaiter.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentWaiter.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflaterView(inflater, container, savedInstanceState);

        fragmentWaiter.onCreateView(rootView);
        windowWaiter.onCreate(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentWaiter.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentWaiter.onStart();
        windowWaiter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentWaiter.onResume();
        windowWaiter.onResume();
    }

    @Override
    public void onPause() {
        fragmentWaiter.onPause();
        windowWaiter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        fragmentWaiter.onStop();
        windowWaiter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        fragmentWaiter.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        fragmentWaiter.onDestroy();
        windowWaiter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        fragmentWaiter.onDetach();
        super.onDetach();

        fragmentWaiter.clear();
        windowWaiter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentWaiter.onSaveInstanceState(outState);
        windowWaiter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fragmentWaiter.onViewStateRestored(savedInstanceState);
        windowWaiter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentWaiter.onActivityResult(requestCode, resultCode, data);
        windowWaiter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fragmentWaiter.onRequestPermissionsResult(requestCode, permissions, grantResults);
        windowWaiter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
