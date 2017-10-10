package com.lpzahd.gallery.context;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lpzahd.gallery.waiter.PreviewWaiter;
import com.lpzahd.waiter.WaiterActivity;


public class PreviewActivity extends WaiterActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PreviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new PreviewWaiter(this));
    }

    @Override
    protected void inflaterView(@Nullable Bundle savedInstanceState) {

    }
}
