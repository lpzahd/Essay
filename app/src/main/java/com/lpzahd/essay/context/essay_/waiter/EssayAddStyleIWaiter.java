package com.lpzahd.essay.context.essay_.waiter;

import android.media.ExifInterface;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.bus.RxBus;
import com.lpzahd.common.taxi.RxTaxi;
import com.lpzahd.common.taxi.Transmitter;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.adapter.ToneItemTouchHelperCallback;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.tone.waiter.WaiterManager;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.common.context.MediaSelectActivity;
import com.lpzahd.essay.common.waiter.MediaSelectWaiter;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.essay.context.preview.PreviewPicActivity;
import com.lpzahd.essay.context.preview.waiter.PreviewPicWaiter;
import com.lpzahd.essay.db.collection.Collection;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.tool.MediaTool;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

import com.lpzahd.aop.api.ThrottleFirst;
import com.lpzahd.waiter.consumer.State;

import org.reactivestreams.Subscriber;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayAddStyleIWaiter extends ToneActivityWaiter<EssayAddActivity>{

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    private EssayAddComponent component;

    public EssayAddStyleIWaiter(EssayAddActivity essayAddActivity) {
        super(essayAddActivity);
        component = new EssayAddComponent(essayAddActivity);
    }

    @Override
    protected void init() {
        super.init();
        component.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
        component.destroy();
    }

    @Override
    protected void initView() {
        View layout = component.inflate(coordinatorLayout);
        coordinatorLayout.addView(layout);
    }

    @Override
    protected void initToolBar() {
        toolBar.setTitle("随笔");
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected int createOptionsMenu(Menu menu) {
        context.getMenuInflater().inflate(R.menu.menu_essay_add, menu);
        return State.STATE_TRUE;
    }

    @Override
    protected int optionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save) {
            if(component.save())  context.delayBackpress();
            return State.STATE_TRUE;
        }

        return super.optionsItemSelected(item);
    }


}
