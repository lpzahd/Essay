package com.lpzahd.essay.context.essay_.waiter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lpzahd.aop.api.ThrottleFirst;
import com.lpzahd.common.bus.Receiver;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay_.EssayAddActivity;
import com.lpzahd.gallery.Gallery;
import com.lpzahd.gallery.context.GalleryActivity;
import com.lpzahd.gallery.presenter.MultiSelectPresenter;
import com.lpzahd.waiter.agency.ActivityWaiter;

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
                .action(new Gallery.ActionWaiter<MultiSelectPresenter>() {
                    @Override
                    public void action(final MultiSelectPresenter waiter) {
                        ToneActivityWaiter<GalleryActivity> simpleWaiter = new ToneActivityWaiter(waiter.getContext()) {
                            @Override
                            protected void initView() {
                                super.initView();
                                waiter.toolBar.setTitle("哈哈哈");
                                waiter.getContext().setSupportActionBar(waiter.toolBar);

                            }
                        };
                        waiter.addWaiter(simpleWaiter);
                    }
                })
                .maxSize(4)
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
