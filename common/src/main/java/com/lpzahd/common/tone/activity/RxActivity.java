package com.lpzahd.common.tone.activity;

import com.lpzahd.Strings;
import com.lpzahd.atool.error.FixedError;
import com.lpzahd.atool.ui.T;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

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
        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        finish();
                    }
                });
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

    /**
     * 默认展示弹窗操作
     */
    protected Consumer<Disposable> showDialogConsumer() {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
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

    public void addDispose(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

}
