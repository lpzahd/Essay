package com.lpzahd.waiter.consumer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 */
public interface IFragment {

    void onAttach(Context context);

    void onCreate(Bundle savedInstanceState);

    void onCreateView(View rootView);

    void onActivityCreated(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroyView();

    void onDestroy();

    void onDetach();

    void onSaveInstanceState(Bundle outState);

    void onViewStateRestored(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
