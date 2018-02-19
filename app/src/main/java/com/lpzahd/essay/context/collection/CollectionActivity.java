package com.lpzahd.essay.context.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.collection.waiter.CollectionWaiter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 : 迪
 * 时间 : 2018/1/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class CollectionActivity extends RxActivity {

    public static final String TAG = "com.lpzahd.essay.context.collection.CollectionActivity";

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
        addActivityWaiter(new CollectionWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        toolBar.setTitle("收藏");
        setSupportActionBar(toolBar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            CollectionEditActivity.startActivity(context);
            return true;
        }

//        if (id == R.id.action_edit) {
//            return true;
//        }
//
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
