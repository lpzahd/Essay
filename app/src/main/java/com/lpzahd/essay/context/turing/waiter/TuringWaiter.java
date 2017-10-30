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
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.lpzahd.Strings;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.turing.TuringActivity;
import com.lpzahd.essay.context.turing.turing123.Turing123;
import com.lpzahd.essay.exotic.retrofit.Net;
import com.lpzahd.essay.tool.ScrollSpeedLinearLayoutManger;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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

    private TuringAdapter mAdapter;

    public TuringWaiter(TuringActivity turingActivity) {
        super(turingActivity);
    }

    @Override
    protected void init() {
        super.init();
        // 要用vollery，炒鸡不爽
//        mTuringManager = new TuringManager(context, "a22cc6ebcb8d48a0a7ad46d189cad393",
//                "3f9a72856c18dc2d");
//        mTuringManager.setHttpRequestListener(listener);
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
        // 增加自己聊天的数据
        mAdapter.add(new TuringModel(TuringHolder.LEFT, message));
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());

        requestTuring(message);
        // 网络交互请求数据
//        mTuringManager.requestTuring(message);

        // 删除文字
        messageEdt.setText("");
    }

    private void requestTuring(String message) {
        Disposable turingDispose = Net.get().turing(message)
                .map(new Function<Turing123, TuringModel>() {
                    @Override
                    public TuringModel apply(@NonNull Turing123 turing123) throws Exception {
                        return new TuringModel(TuringHolder.RIGHT, turing123.getText());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TuringModel>() {
                    @Override
                    public void accept(TuringModel turingModel) throws Exception {
                        if (mAdapter != null) {
                            mAdapter.add(turingModel);
                            recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        T.t("error : " + throwable.getMessage());
                    }
                });

        context.addDispose(turingDispose);

    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    class TuringModel {
        int type;
        Uri head = Frescoer.res(R.mipmap.ic_head_haughty_rabbit);
        String msg;

        TuringModel(int type, String msg) {
            this.type = type;
            this.msg = msg;
        }
    }

    abstract static class TuringHolder extends ToneAdapter.ToneHolder {

        static final int LEFT = 1;
        static final int RIGHT = 2;

        @BindView(R.id.header_drawee_view)
        SimpleDraweeView headerDraweeView;

        @BindView(R.id.message_tv)
        AppCompatTextView messageTv;

        TuringHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class TuringLeftHolder extends TuringHolder {

        TuringLeftHolder(View itemView) {
            super(itemView);
        }
    }

    private static class TuringRightHolder extends TuringHolder {

        TuringRightHolder(View itemView) {
            super(itemView);
        }
    }

    private static class TuringAdapter extends ToneAdapter<TuringModel, TuringHolder> {

        private int size = 200;

        TuringAdapter(Context context) {
            super(context);
            size = Ui.dip2px(context, 48);
        }

        @Override
        public int getItemViewType(int position) {
            return getData().get(position).type;
        }

        @Override
        public TuringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TuringHolder.LEFT:
                    return new TuringLeftHolder(inflateItemView(R.layout.item_turing_left, parent));
                case TuringHolder.RIGHT:
                default:
                    return new TuringRightHolder(inflateItemView(R.layout.item_turing_right, parent));
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
