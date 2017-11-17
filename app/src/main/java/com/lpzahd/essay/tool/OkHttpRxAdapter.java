package com.lpzahd.essay.tool;

import com.lpzahd.atool.ui.T;

import java.io.IOException;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Author : Lpzahd
 * Date : 六月
 * Desction :
 */
public class OkHttpRxAdapter extends Observable<Response> {

    private final Call call;

    private OkHttpRxAdapter(Call call) {
        this.call = call;
    }

    public static Observable<Response> adapter(final Call call) {
        return new OkHttpRxAdapter(call);
    }

    @Override
    protected void subscribeActual(Observer<? super Response> observer) {
        RequestDispose requestDispose = new RequestDispose(call, observer);
        observer.onSubscribe(requestDispose);
        call.enqueue(requestDispose);
    }

    private static final class RequestDispose implements Disposable, Callback {

        private final Call call;
        private final Observer<? super Response> observer;

        RequestDispose(Call call, Observer<? super Response> observer) {
            this.call = call;
            this.observer = observer;
        }

        @Override
        public void dispose() {
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return call.isCanceled();
        }

        @Override
        public void onFailure(Call call, IOException e) {
            T.post(e.getMessage());
            dispose();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!isDisposed()) {
                observer.onNext(response);
            }
        }
    }

}
