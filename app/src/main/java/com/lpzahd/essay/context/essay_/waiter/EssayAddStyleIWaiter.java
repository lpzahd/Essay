package com.lpzahd.essay.context.essay_.waiter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.lpzahd.aop.api.ThrottleFirst;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.presenter.MultiSelectPresenter;
import com.lpzahd.waiter.agency.ActivityWaiter;
import com.lpzahd.waiter.consumer.State;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayAddStyleIWaiter extends ToneActivityWaiter<EssayAddActivity> {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @BindView(R.id.title_edt)
    AppCompatEditText titleEdt;

    @BindView(R.id.content_edt)
    AppCompatEditText contentEdt;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.record_iv)
    AppCompatImageView recordIv;

    @BindView(R.id.image_iv)
    AppCompatImageView imageIv;

    @BindView(R.id.play_iv)
    AppCompatImageView playIv;

    public EssayAddStyleIWaiter(EssayAddActivity essayAddActivity) {
        super(essayAddActivity);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        toolBar.setTitle("随笔");
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        context.setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @ThrottleFirst
    @OnClick(R.id.image_iv)
    public void openGallery(View view) {
        Gallery.with(context)
                .image()
                .maxSize(4)
                .addWaiter(new ActivityWaiter<GalleryActivity, ActivityWaiter>() {

                    @Override
                    protected void init() {
//                        context.setTheme(R.style.AppTheme_AppBarOverlay);
                    }

                    @Override
                    protected void create(Bundle savedInstanceState) {
//                        MultiSelectPresenter waiter = (MultiSelectPresenter) getBoss();
//                        waiter.toolBar.setPopupTheme(R.style.AppTheme_PopupOverlay);
//                        waiter.toolBar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
//                        waiter.toolBar.setTitle("图片");
//                        waiter.toolBar.inflateMenu(R.menu.menu_gallery_multi_select);
//                        waiter.toolBar.setMenuCallbacks();
//                        context.setSupportActionBar(waiter.toolBar);
//                        R.layout.abc_action_menu_layout,
//                                R.layout.abc_action_menu_item_layout,
//                        android.support.v7.view.menu.ActionMenuItemView view1 = null;
//                        createOptionsMenu(waiter.toolBar.getMenu());
                    }

//                    @Override
//                    protected int createOptionsMenu(Menu menu) {
//                        context.getMenuInflater().inflate(R.menu.menu_gallery_multi_select, menu);
//                        return State.STATE_TRUE;
//                    }
//
//                    @Override
//                    protected int optionsItemSelected(MenuItem item) {
//                        int id = item.getItemId();
//
//                        if(id == R.id.action_select) {
//                            T.t("-,-");
//                            Random random = new Random();
//                            item.setTitle("" + random.nextInt(20));
//                        }
//
//                        return super.optionsItemSelected(item);
//                    }
                })
                .subscribe(new Receiver() {
                    @Override
                    public <T> void receive(Flowable<T> flowable) {
                        flowable.subscribe(new Consumer<T>() {
                            @Override
                            public void accept(T t) throws Exception {

                            }
                        });
                    }
                })
                .openGallery();
    }
}
