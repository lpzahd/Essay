package com.lpzahd.essay.context.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lpzahd.essay.R;
import com.lpzahd.essay.context.note.waiter.StudySearchWaiter;
import com.lpzahd.common.tone.activity.ToneActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudyActivity extends ToneActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    public void init() {
        super.init();
        addWindowWaiter(new StudySearchWaiter());
    }

    @Override
    protected void inflaterView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_study);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

}
