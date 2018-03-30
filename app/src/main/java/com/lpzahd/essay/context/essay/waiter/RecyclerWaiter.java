package com.lpzahd.essay.context.essay.waiter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.app.App;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay.EssayAddDialog;
import com.lpzahd.essay.context.essay.EssayEditDialog;
import com.lpzahd.essay.context.essay.EssayPop;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.data.DataFactory;
import com.lpzahd.common.tone.waiter.RefreshBusWaiter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.tool.DateTime;
import com.lpzahd.view.DraweeForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class RecyclerWaiter extends ToneActivityWaiter<EssayActivity> implements DataFactory.DataProcess<Essay, RecyclerWaiter.Model>, EssayAddDialog.InputCallback {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private EssayPopWrap pop;
    private EssayAddDialog essayAddDialog;

    private EssayAdapter adapter;

    private SwipeRefreshWaiter refreshWaiter;

    private DataFactory<Essay, Model> mFactory;

    private Realm mRealm;
    private RealmResults<Essay> mEssays;

    public RecyclerWaiter(EssayActivity essayActivity) {
        super(essayActivity);
    }

    @Override
    protected void init() {
        super.init();
        addWindowWaiter(new RefreshBusWaiter(new RefreshBusWaiter.CaughtEvent() {
            @Override
            public void caught() {
                if(refreshWaiter != null) refreshWaiter.autoRefresh();
            }
        }));

        mFactory = DataFactory.of(this);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new EssayAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(itemTouchListener());

        SwipeRefreshLayout swipeRefreshLayout = Ui.findViewById(rootView, R.id.swipe_refresh_layout);
        RecyclerView recyclerView = Ui.findViewById(rootView, R.id.recycler_view);
        addWindowWaiter(refreshWaiter = new SwipeRefreshWaiter(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<? extends List> doRefresh(int page) {
                return mRealm.where(Essay.class)
                        .sort("date", Sort.DESCENDING)
                        .findAllAsync()
                        .asFlowable()
                        .filter(new Predicate<RealmResults<Essay>>() {
                            @Override
                            public boolean test(RealmResults<Essay> essays) throws Exception {
                                return essays.isLoaded();
                            }
                        })
                        .map(new Function<RealmResults<Essay>, List>() {
                            @Override
                            public List apply(RealmResults<Essay> essays) throws Exception {
                                mEssays = essays;
                                return mFactory.processArray(essays);
                            }
                        });
//                essays = realm.where(Essay.class)
//                        .findAllSorted("date", Sort.DESCENDING);
//                return Flowable.just(essays)
//                        .map(new Function<RealmResults<Essay>, List>() {
//                            @Override
//                            public List apply(@io.reactivex.annotations.NonNull RealmResults<Essay> essays) throws Exception {
//                                return factory.processArray(essays);
//                            }
//                        });
            }

        });

        pop = new EssayPopWrap();

        essayAddDialog = new EssayAddDialog();
        essayAddDialog.setInputCallback(this);
    }

    @NonNull
    private OnItemHolderTouchListener<EssayHolder> itemTouchListener() {
        return new OnItemHolderTouchListener<EssayHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, EssayHolder holder) {
                super.onClick(rv, holder);
                final int position = holder.getAdapterPosition();
                Model model = adapter.getItem(position);
                EssayEditDialog dialog = EssayEditDialog.newEssaylDialog(model.title, model.content);
                dialog.setInputCallback(new EssayEditDialog.InputCallback() {
                    @Override
                    public void onInput(EssayEditDialog dialog, CharSequence title, CharSequence content) {
                        Model model = adapter.getItem(position);

                        if (!Strings.equals(title, model.title) || !Strings.equals(content, model.content)) {
                            model.title = title.toString();
                            model.content = content.toString();

                            mRealm.beginTransaction();
                            Essay essay = mEssays.get(position);
                            essay.setTitle(model.title);
                            essay.setContent(model.content);
                            mRealm.commitTransaction();

                            adapter.notifyItemChanged(position);
                        }

                    }
                });
                dialog.show(context);
            }

            @Override
            public void onLongClick(RecyclerView rv, EssayHolder holder) {
                super.onLongClick(rv, holder);
                pop.show(holder);
            }
        };
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void resume() {
        super.resume();
        refreshWaiter.autoRefresh();
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }

    @OnClick(R.id.fab)
    void showEssayAddDialog() {
        essayAddDialog.show(context);
    }

    @Override
    public void onInput(EssayAddDialog dialog, CharSequence title, CharSequence content) {
        final Essay essay = new Essay();
        essay.setTitle(title.toString());
        essay.setContent(content.toString());
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(essay);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                recyclerView.scrollToPosition(0);
                adapter.addFirst(mFactory.process(essay));
            }
        });
    }

    @Override
    public Model process(Essay essay) {
        Model model = new Model();
        model.title = essay.getTitle();
        model.content = essay.getContent();
        model.date = DateTime.format(essay.getDate());

        if (essay.geteFile() == null || Lists.empty(essay.geteFile().getImages())) {
            model.photos = Collections.emptyList();
        } else {
            RealmList<Image> images = essay.geteFile().getImages();
            model.photos = new ArrayList<>(images.size());
            for (Image image : images) {
                DraweeForm.Photo photo = new DraweeForm.Photo(image.getPath(), image.getWidth(), image.getHeight());
                model.photos.add(photo);
            }
        }
        return model;
    }

    static class Model {
        String title;
        String content;
        String date;
        List<DraweeForm.Photo> photos;
    }

    class EssayHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.title_tv)
        AppCompatTextView titleTv;

        @BindView(R.id.content_tv)
        AppCompatTextView contentTv;

        @BindView(R.id.date_tv)
        AppCompatTextView dateTv;

        @BindView(R.id.drawee_form)
        DraweeForm draweeForm;

        private EssayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class EssayAdapter extends ToneAdapter<Model, EssayHolder> {

        private EssayAdapter(Context context) {
            super(context);
        }

        @Override
        public EssayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EssayHolder(inflateItemView(R.layout.item_essay, parent));
        }

        @Override
        public void onBindViewHolder(EssayHolder holder, int position) {
            Model model = getItem(position);
            holder.titleTv.setText(model.title);
            holder.contentTv.setText(model.content);
            holder.dateTv.setText(model.date);
            holder.draweeForm.update(model.photos);
        }
    }

    private class EssayPopWrap extends EssayPop {

        EssayHolder holder;

        void show(EssayHolder holder) {
            this.holder = holder;
            showEssayPop(holder.itemView);
        }

        EssayPopWrap() {
            init();
        }

        private void init() {
            setOnItemClickListener(new OnItemClick() {
                @Override
                public void onClick(RecyclerView rv, int position) {
                    pop.dismiss();
                    if (recyclerView == null || holder == null) return;

                    final int index = holder.getAdapterPosition();
                    switch (position) {
                        case 0:
                            putEssayTag(index);
                            break;
                        case 1:
                            putEssayImg(index);
                            break;
                        case 2:
                            putEssayIsShow(index);
                            break;
                        case 3:
                            saveEssayItem(holder.itemView, index);
                            break;
                        case 4:
                            copyEssayContent(index);
                            break;
                        case 5:
                            deleteEssayItem(index);
                            break;
                    }
                }
            });
        }

        void putEssayTag(int position) {
            //TODO
            T.t("天机不可泄露");
        }

        void putEssayImg(int position) {
            //TODO
            T.t("天机不可泄露");
        }

        void putEssayIsShow(int position) {
            //TODO
            T.t("天机不可泄露");
        }

        void saveEssayItem(View view, int position) {
            final Bitmap bitmap = Ui.getDrawingCache(view);
            ImageView iv = new ImageView(context);
            iv.setImageBitmap(bitmap);
            new MaterialDialog.Builder(context)
                    .title("保存")
                    .customView(iv, true)
                    .negativeText("取消")
                    .positiveText("保存这张图片")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            saveCacheBitmap(bitmap);
                        }
                    })
                    .buttonsGravity(GravityEnum.CENTER)
                    .show();
        }

        void copyEssayContent(int position) {
            Model model = adapter.getData().get(position);
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(model.title)) {
                builder.append(model.title).append("\n");
            }
            if (!TextUtils.isEmpty(model.content)) {
                builder.append(model.content).append("\n");
            }
            builder.append(model.date);

            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) App.getApp()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText(null, builder));

            T.t("内容复制完成");
        }

        void deleteEssayItem(final int position) {
            Model model = adapter.getData().get(position);
            String tipTxt;
            if (!Strings.empty(model.title)) {
                tipTxt = model.title;
            } else if (!Strings.empty(model.content)) {
                if (model.content.length() > 12) {
                    tipTxt = Strings.join(model.content.substring(0, 12), "...");
                } else {
                    tipTxt = model.content;
                }
            } else {
                tipTxt = Strings.join("第", String.valueOf(position), "个");
            }
            new MaterialDialog.Builder(context)
                    .iconRes(R.drawable.ic_warning_black_24dp)
                    .title("删除")
                    .content(Strings.join("你确定要把[", tipTxt, "]删除嘛"))
                    .negativeText(R.string.tip_negative)
                    .positiveText(R.string.tip_positive)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    mEssays.get(position).deleteFromRealm();
                                    adapter.remove(position);
                                }
                            });
                        }
                    })
                    .show();
        }

    }

    /**
     * 保存图片
     */
    private void saveCacheBitmap(Bitmap bitmap) {
        Observable.just(bitmap)
                .map(new Function<Bitmap, Void>() {
                    @Override
                    public Void apply(Bitmap bitmap) throws Exception {
                        String fileName = Keeper.getF().getFilePath(Files.Scope.PHOTO_RAW, String.valueOf(new Date().getTime()));
                        Keeper.getBt().save(bitmap, fileName);
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        T.t("开始保存图片");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) throws Exception {
                        T.t("保存完毕");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.t("error : " + throwable.getMessage());
                    }
                });
    }
}
