package com.lpzahd.essay.context.essay.waiter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lpzahd.Lists;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.data.DataFactory;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay.EssayAddDialog;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.essay.tool.DateTime;
import com.lpzahd.view.DraweeForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 作者 : 迪
 * 时间 : 2017/9/24.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class EssayStyleIWaiter extends ToneActivityWaiter<EssayActivity> implements DataFactory.DataProcess<Essay,EssayStyleIWaiter.EssayModel> {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Realm mRealm;

    private RealmResults<Essay> mEssays;

    private DataFactory<Essay, EssayModel> mFactoty;

    EssayAdapter mAdapter;

    private SwipeRefreshWaiter mRefreshWaiter;

    private EssayAddDialog mEssayAddDialog;

    public EssayStyleIWaiter(EssayActivity essayActivity) {
        super(essayActivity);
    }

    @Override
    protected void init() {
        super.init();
        mFactoty = DataFactory.of(this);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void initView() {
        super.initView();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new EssayAdapter(context);
        recyclerView.setAdapter(mAdapter);

        addWindowWaiter(mRefreshWaiter = new SwipeRefreshWaiter(swipeRefreshLayout, recyclerView) {

            @Override
            public Flowable<? extends List> doRefresh(int page) {
                mEssays = mRealm.where(Essay.class)
                        .findAllSorted("date", Sort.DESCENDING);
                return Flowable.just(mEssays)
                        .map(new Function<RealmResults<Essay>, List>() {
                            @Override
                            public List apply(@io.reactivex.annotations.NonNull RealmResults<Essay> essays) throws Exception {
                                return mFactoty.processArray(essays);
                            }
                        });
            }

        });

        mRefreshWaiter.autoRefresh();

        mEssayAddDialog = new EssayAddDialog();
        mEssayAddDialog.setInputCallback(new EssayAddDialog.InputCallback() {
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
                        mAdapter.addFirst(mFactoty.process(essay));
                    }
                });
            }
        });
    }

    @OnClick(R.id.fab)
    void showEssayAddDialog() {
        mEssayAddDialog.show(context);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }

    @Override
    public EssayModel process(Essay essay) {
        EssayModel model = new EssayModel();
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

    static class EssayModel {
        String title;
        String content;
        String date;
        List<DraweeForm.Photo> photos;
    }

    static class EssayHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.content_tv)
        AppCompatTextView contentTv;
        @BindView(R.id.illustrated_iv)
        AppCompatImageView illustratedIv;
        @BindView(R.id.date_tv)
        AppCompatTextView dateTv;

        public EssayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class EssayAdapter extends ToneAdapter<EssayModel, EssayHolder> {

        public EssayAdapter(Context context) {
            super(context);
        }

        @Override
        public EssayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EssayHolder(inflateItemView(R.layout.item_essay_style_01, parent));
        }

        @Override
        public void onBindViewHolder(EssayHolder holder, int position) {
            EssayModel model = getItem(position);
            holder.contentTv.setText(model.title + "\n\n" + model.content);
            holder.dateTv.setText(model.date);

            Bitmap bm = BitmapFactory
                    .decodeResource(context.getResources(), R.mipmap.ic_failure_image);
            Drawable[] array = new Drawable[3];
            array[0] = new PaintDrawable(Color.GRAY); // 黑色
            array[1] = new PaintDrawable(Color.GREEN); // 白色
            array[2] = new PaintDrawable(Color.RED);
//            array[2] = new BitmapDrawable(bm); // 位图资源
            LayerDrawable layerDrawable = new LayerDrawable(array); // 参数为上面的Drawable数组
            layerDrawable.setLayerInset(0,0,0,0,0);
            layerDrawable.setLayerInset(1,20,20,60,60);
            layerDrawable.setLayerInset(2,20,30,60,60);

            holder.illustratedIv.setImageDrawable(layerDrawable);
        }
    }

}
