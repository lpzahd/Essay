package com.lpzahd.common.tone.activity;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lpzahd.Strings;
import com.lpzahd.atool.error.FixedError;
import com.lpzahd.atool.ui.L;
import com.lpzahd.atool.ui.T;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.R;
import com.lpzahd.common.kangna.KangNaOnCompleteObservable;
import com.lpzahd.view.KangNaView;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class RxActivity extends ToneActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public void delayBackpress() {
        delayBackpress(500L);
    }

    public void delayBackpress(long delayTime) {
        addDispose(Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        finish();
                    }
                }));
    }

    /**
     * normal action
     */
    public <D> void rxAction(Observable<D> observable, Consumer<D> consumer) {
        addDispose(observable
                .doOnSubscribe(showDialogConsumer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, simpleExcConsumer(), simpleCompAction()));
    }

    public <D> void rxAction(Flowable<D> flowable, Consumer<D> consumer) {
        addDispose(flowable
                .doOnSubscribe(showDialogConsumer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, simpleExcConsumer(), simpleCompAction()));
    }

    /**
     * 默认展示弹窗操作
     */
    protected <D> Consumer<D> showDialogConsumer() {
        return new Consumer<D>() {
            @Override
            public void accept(D d) throws Exception {
                showDialog();
            }
        };
    }

    /**
     * 默认错误处理操作
     */
    protected Consumer<Throwable> simpleExcConsumer() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                dismiss();
                FixedError fixedError = FixedError.FixedErrorHandler.handleError(throwable);
                T.t(Strings.join("error : ", fixedError.getMessage()));
            }
        };
    }

    /**
     * 默认处理的结束操作
     */
    protected Action simpleCompAction() {
        return new Action() {
            @Override
            public void run() throws Exception {
                dismiss();
            }
        };
    }

    public void disposeSafely(Disposable disposable) {
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

    public void addDispose(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private MaterialDialog mKangNaDilaog;
    private long mKangNaShowTime;

    public MaterialDialog kangNaDialog() {
        if(mKangNaDilaog == null) {
            mKangNaDilaog = new MaterialDialog.Builder(context)
                    .customView(R.layout.view_kangna, true)
                    .backgroundColor(0x01000000) // 源代码中校验了0，无法全透明
                    .build();

            final KangNaViewControl control = new KangNaViewControl();

            mKangNaDilaog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    final KangNaView kangNaView = (KangNaView) mKangNaDilaog.getCustomView().findViewById(R.id.kang_na_view);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(Ui.dip2px(context, 2));
                    drawable.setColor(R.attr.md_background_color);
                    kangNaView.setBackground(drawable);

                    control.setKangNaView(kangNaView);
                    control.autoRandomAnim(5, 1000);

                    mKangNaShowTime = System.currentTimeMillis();
                }
            });

            mKangNaDilaog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    control.disposs();
                }
            });
        }
        return mKangNaDilaog;
    }

    public void showKangNaDialog() {
        kangNaDialog().show();
    }

    public void dismissKangNaDialog() {
        long offset = System.currentTimeMillis() - mKangNaShowTime;
        if(offset < 3000) {
            Observable.timer(3000 - offset, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            kangNaDialog().dismiss();
                        }
                    });
        } else {
            kangNaDialog().dismiss();
        }
    }


    private static class KangNaViewControl {

        static List<String[]> txts = new ArrayList<>();

        static {
            txts.add(new String[] {
                    "△", "▽", "△", "▽","△"
            });

            txts.add(new String[] {
                    "1", "+", "2", "=", "?"
            });

            txts.add(new String[] {
                    "→", "↓", "↑", "↓", "←"
            });
        }

        private Random random = new Random();

        private KangNaView kangNaView;

        private Disposable disposable;

        private String[] random() {
            return txts.get(random.nextInt(txts.size()));
        }

        public static KangNaViewControl attachKangNaView(KangNaView kangNaView) {
            KangNaViewControl control = new KangNaViewControl();
            control.kangNaView = kangNaView;
            return control;
        }

        public void setKangNaView(KangNaView kangNaView) {
            this.kangNaView = kangNaView;
        }

        public Disposable autoRandomAnim(int repeat, long interval) {
            disposs();
            kangNaView.setTexts(true, random());
            return disposable = new KangNaOnCompleteObservable(kangNaView)
                    .subscribeOn(Schedulers.io())
                    .delay(interval, TimeUnit.MILLISECONDS)
                    .take(repeat - 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            kangNaView.setTexts(true, random());
                            L.e("o ");
                        }
                    });

        }

        public void disposs() {
            if(disposable != null && !disposable.isDisposed())
                disposable.dispose();
        }

    }
}
