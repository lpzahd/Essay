package com.lpzahd.essay.tool;

import android.os.Looper;
import com.jakewharton.rxbinding2.internal.Notification;
import com.lpzahd.view.KangNaView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * 作者 : 迪
 * 时间 : 2017/10/30.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public final class KangNaOnCompleteObservable extends Observable<Object> {

    private final KangNaView view;

    KangNaOnCompleteObservable(KangNaView view) {
        this.view = view;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(view, observer);
        observer.onSubscribe(listener);
        view.setOnKangNaCompleteListener(listener);
    }

    static final class Listener extends MainThreadDisposable implements KangNaView.OnKangNaCompleteListener {
        private final KangNaView view;
        private final Observer<? super Object> observer;

        Listener(KangNaView view, Observer<? super Object> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override protected void onDispose() {
            view.setOnClickListener(null);
        }

        @Override
        public void onComplete(KangNaView kangNaView) {
            if (!isDisposed()) {
                observer.onNext(Notification.INSTANCE);
            }
        }
    }

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onError(new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName()));
            return false;
        }
        return true;
    }
}
