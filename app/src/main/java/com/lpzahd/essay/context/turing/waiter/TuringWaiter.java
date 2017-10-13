package com.lpzahd.essay.context.turing.waiter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.lpzahd.Objects;
import com.lpzahd.Strings;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.turing.TuringActivity;
import com.lpzahd.essay.tool.ScrollSpeedLinearLayoutManger;
import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class TuringWaiter extends ToneActivityWaiter<TuringActivity> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.message_edt)
    AppCompatEditText messageEdt;

    @BindView(R.id.send_message_btn)
    AppCompatTextView sendMessageBtn;

    private TuringManager mTuringManager;

    private TuringAdapter mAdapter;

    private List<TuringModel> turings;

    public TuringWaiter(TuringActivity turingActivity) {
        super(turingActivity);
    }

    @Override
    protected void initView() {
        ScrollSpeedLinearLayoutManger manager = new ScrollSpeedLinearLayoutManger(context);
        manager.setSpeedSlow();

        // 保持聊天消息持续在底部
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        mAdapter = new TuringAdapter(context);
        recyclerView.setAdapter(mAdapter);

        RxView.clicks(sendMessageBtn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        sendMessage(messageEdt.getText().toString());
                    }
                });

        RxTextView.afterTextChangeEvents(messageEdt)
                .subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) throws Exception {
                        if (Strings.empty(textViewAfterTextChangeEvent.editable())) {
                            if (sendMessageBtn.isEnabled())
                                sendMessageBtn.setEnabled(false);
                        } else {
                            if (!sendMessageBtn.isEnabled())
                                sendMessageBtn.setEnabled(true);
                        }
                    }
                });
    }

    private void sendMessage(String message) {

        // Ui.hideInput(ChatActivity.this);

        // 增加自己聊天的数据
//        mAdapter.add(new Chat(Chat.RIGHT, mineHeader, message));
//        recyclerView.smoothScrollToPosition(adapter.getItemCount());
//
//        // 网络交互请求数据
//        mTuringManager.requestTuring(message);
//
//        // 删除文字
//        messageEdt.setText("");
    }

    public class Turing {
        /**
         * code : 20000
         * text : 亲
         * url : http://baidu.com/img
         */

        private int code;
        private String text;
        private String url;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private HttpRequestListener listener = new HttpRequestListener() {

        @Override
        public void onSuccess(String result) {
            if (result != null) {
                Observable.just(result)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<String, Turing>() {
                            @Override
                            public Turing apply(@NonNull String s) throws Exception {
                                return new Gson().fromJson(s, Turing.class);
                            }
                        })
                        .filter(new Predicate<Turing>() {
                            @Override
                            public boolean test(@NonNull Turing turing) throws Exception {
                                return !Objects.isNull(turing);
                            }
                        })
                        .map(new Function<Turing, TuringModel>() {
                            @Override
                            public TuringModel apply(@NonNull Turing turing) throws Exception {
                                TuringModel model = new TuringModel();
                                model.msg = turing.getText();
                                return model;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<TuringModel>() {
                            @Override
                            public void accept(TuringModel turingModel) throws Exception {
                                if(mAdapter != null) {
                                    mAdapter.add(turingModel);
                                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                                }
                            }
                        });
            }
        }

        @Override
        public void onFail(int code, final String error) {
            Observable.just(error)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            T.t("error : " + error);
                        }
                    });
        }
    };

    @Override
    protected void destroy() {
        super.destroy();
        mTuringManager.setHttpRequestListener(null);
        listener = null;
        mTuringManager = null;
    }

    public class TuringModel {
        public Uri head;
        public String msg;
    }

    public abstract static class TuringHolder extends ToneAdapter.ToneHolder {

        public static final int LEFT = 1;
        public static final int RIGHT = 2;

        @BindView(R.id.header_drawee_view)
        SimpleDraweeView headerDraweeView;

        @BindView(R.id.message_tv)
        AppCompatTextView messageTv;

        public TuringHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class TuringLeftHolder extends TuringHolder {

        public TuringLeftHolder(View itemView) {
            super(itemView);
        }
    }

    public static class TuringRightHolder extends TuringHolder {

        public TuringRightHolder(View itemView) {
            super(itemView);
        }
    }

    public static class TuringAdapter extends ToneAdapter<TuringModel, TuringHolder> {

        private int size = 200;

        public TuringAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 48);
        }

        @Override
        public TuringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TuringHolder.LEFT:
                    return new TuringLeftHolder(inflateItemView(R.layout.item_turing_left, parent));
                case TuringHolder.RIGHT:
                default:
                    return new TuringRightHolder(inflateItemView(R.layout.item_turing_left, parent));
            }
        }

        @Override
        public void onBindViewHolder(TuringHolder holder, int position) {
            TuringModel model = getItem(position);

            holder.messageTv.setText(model.msg);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(model.head)
                    .setResizeOptions(ResizeOptions.forSquareSize(size))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.headerDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            holder.headerDraweeView.setController(controller);
        }
    }
}
