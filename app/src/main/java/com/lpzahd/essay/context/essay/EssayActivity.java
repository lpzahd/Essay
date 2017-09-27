package com.lpzahd.essay.context.essay;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay.waiter.EssayStyleIIWaiter;
import com.lpzahd.essay.context.essay.waiter.EssayStyleIWaiter;
import com.lpzahd.essay.context.essay.waiter.RecyclerWaiter;
import com.lpzahd.common.tone.activity.RxActivity;
import com.lpzahd.essay.context.essay_.EssayAddActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class EssayActivity extends RxActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EssayActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init() {
        super.init();
//        addActivityWaiter(new RecyclerWaiter(this));
//        addActivityWaiter(new EssayStyleIWaiter(this));
        addActivityWaiter(new EssayStyleIIWaiter(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_essay);
        ButterKnife.bind(this);
        toolBar.setTitle("随笔");
        setSupportActionBar(toolBar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_essay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            EssayAddActivity.startActivity(context);
            return true;
        }

        if (id == R.id.action_edit) {
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
