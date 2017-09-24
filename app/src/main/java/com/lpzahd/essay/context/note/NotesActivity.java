package com.lpzahd.essay.context.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lpzahd.essay.R;
import com.lpzahd.essay.context.note.waiter.NotesWaiter;
import com.lpzahd.common.tone.activity.ToneActivity;

import butterknife.BindView;

public class NotesActivity extends ToneActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    public void init() {
        super.init();
        addWindowWaiter(new NotesWaiter());
    }

    @Override
    protected void inflaterView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_notes);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

}
