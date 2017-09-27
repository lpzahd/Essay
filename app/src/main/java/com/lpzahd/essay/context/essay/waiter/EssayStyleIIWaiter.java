package com.lpzahd.essay.context.essay.waiter;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.data.DataFactory;
import com.lpzahd.common.tone.fragment.ToneDialogFragment;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.waiter.refresh.SwipeRefreshWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.essay.EssayAddDialog;
import com.lpzahd.essay.context.essay.EssayStyleIIAddDialog;
import com.lpzahd.essay.db.essay.Essay;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.essay.tool.DateTime;
import com.lpzahd.view.DraweeForm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class EssayStyleIIWaiter extends ToneActivityWaiter<EssayActivity> implements DataFactory.DataProcess<Essay, EssayStyleIIWaiter.EssayModel>,EssayStyleIIAddDialog.InputCallback {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Realm mRealm;

    private RealmResults<Essay> mEssays;

    private DataFactory<Essay, EssayStyleIIWaiter.EssayModel> mFactoty;

    private EssayAdapter mAdapter;

    private SwipeRefreshWaiter mRefreshWaiter;

    public EssayStyleIIWaiter(EssayActivity essayActivity) {
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

        recyclerView.setPadding(16,0,16,0);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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


    }

    @OnClick(R.id.fab)
    void showEssayAddDialog() {
        EssayStyleIIAddDialog dialog = new EssayStyleIIAddDialog();
        dialog.setInputCallback(this);
        dialog.show(context);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }

    @Override
    public EssayStyleIIWaiter.EssayModel process(Essay essay) {
        EssayStyleIIWaiter.EssayModel model = new EssayStyleIIWaiter.EssayModel();
        model.title = essay.getTitle();
        model.content = essay.getContent();
        model.date = DateTime.format(essay.getDate(), "yyyy-MM-dd");
        model.gravity = parseContent(model.content);
        return model;
    }

    private
    @EssayStyleIIWaiter.GRAVITY
    int parseContent(String content) {
        if (Strings.empty(content)) return GRAVITY_LEFT;

        if (content.length() < 10) return GRAVITY_CENTER;

        String[] lines = content.split("\n");
        boolean isPoet = true;
        int len = lines[0].length();
        for (int i = 1; i < lines.length; i++) {
            if(len != lines[i].length()) {
                isPoet = false;
                break;
            }
        }
        return isPoet ? GRAVITY_CENTER : GRAVITY_LEFT;
    }

    @Override
    public void onInput(EssayStyleIIAddDialog dialog, CharSequence title, CharSequence content) {
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

    static class EssayModel {
        String title;
        String content;
        String date;
        @GRAVITY
        int gravity;
    }

    private static final int GRAVITY_LEFT = 1;
    private static final int GRAVITY_CENTER = 2;

    @IntDef({GRAVITY_LEFT, GRAVITY_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    private @interface GRAVITY {}

    static class EssayHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.top_layout)
        RelativeLayout topLayout;

        @BindView(R.id.bottom_layout)
        RelativeLayout bottomLayout;

        @BindView(R.id.date_tv)
        AppCompatTextView dateTv;

        @BindView(R.id.title_tv)
        AppCompatTextView titleTv;

        @BindView(R.id.content_tv)
        AppCompatTextView contentTv;

        EssayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            final Context context = itemView.getContext();
            GradientDrawable topDrawable = new GradientDrawable();
            topDrawable.setCornerRadii(new float[]{
                    16f, 16f, 16f, 16f, 0f, 0f, 0f, 0f
            });
            topDrawable.setColor(Color.parseColor("#e84887"));
            topLayout.setBackground(topDrawable);

            GradientDrawable bottomDrawable = new GradientDrawable();
            bottomDrawable.setCornerRadii(new float[]{
                    0f, 0f, 0f, 0f, 16f, 16f, 16f, 16f
            });
            bottomDrawable.setColor(Color.parseColor("#f7f7f7"));
            bottomLayout.setBackground(bottomDrawable);
        }
    }

    private class EssayAdapter extends ToneAdapter<EssayModel, EssayHolder> {

        EssayAdapter(Context context) {
            super(context);
        }

        @Override
        public EssayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EssayHolder(inflateItemView(R.layout.item_essay_style_02, parent));
        }

        @Override
        public void onBindViewHolder(EssayHolder holder, int position) {
            EssayModel model = getItem(position);
            holder.dateTv.setText(model.date);
            if (Strings.empty(model.title)) {
                holder.titleTv.setVisibility(View.GONE);
            } else {
                holder.titleTv.setVisibility(View.VISIBLE);
                holder.titleTv.setText(model.title);
            }

            holder.contentTv.setText(model.content);
            if(model.gravity == GRAVITY_LEFT) {
                holder.contentTv.setGravity(Gravity.START);
            } else {
                holder.contentTv.setGravity(Gravity.CENTER);
            }
        }
    }
}
