package com.lpzahd.atool.keeper.storage;

import com.lpzahd.atool.keeper.storage.interceptor.RealDownloadTask;
import com.lpzahd.atool.keeper.storage.internal.NamedRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class DownloadTask implements Task {

    private final Storage storage;
    private final Config config;

    // Guarded by this.
    private boolean executed;

    private Progress progress;

    public DownloadTask(Storage storage, Config config) {
        this.storage = storage;
        this.config = config;
        progress = new Progress();
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public Result execute() throws IOException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        try {
            storage.dispatcher().executed(this);
            Result result = getFileWithInterceptorConvert(null);
            if (result == null) throw new IOException("Failed");
            return result;
        } finally {
            storage.dispatcher().finished(this);
        }
    }

    @Override
    public void enqueue(CallBack callBack) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }

        storage.dispatcher().enqueue(new DownloadTask.AsyncTask(callBack));
    }

    @Override
    public void cancel() {
        //TODO 多线程的时候再取消吧
        progress.status = Progress.Status.CANCEL;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCanceled() {
        return progress.status == Progress.Status.CANCEL;
    }

    @Override
    public Task clone() {
        return null;
    }

    private Result getFileWithInterceptorConvert(CallBack callBack) throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(storage.interceptors());

        RealDownloadTask task = new RealDownloadTask(this, progress, interceptors, callBack);
        return task.exec();
    }

    final class AsyncTask extends NamedRunnable {

        private final CallBack callback;

        AsyncTask(CallBack callback) {
            super("OkHttp %s", config.getUrl());
            this.callback = callback;
        }

        Config config() {
            return config;
        }

        String name() {
            return config.getName();
        }


        DownloadTask get() {
            return DownloadTask.this;
        }

        @Override protected void execute() {
            callback.onStart(DownloadTask.this);
            try {
                Result result = getFileWithInterceptorConvert(callback);
                if (result == null) {
                    callback.onFailure(DownloadTask.this, new IOException("Failed"));
                } else {
                    callback.onSuccess(DownloadTask.this, result);
                }

            } catch (Exception e) {
                callback.onFailure(DownloadTask.this, new IOException("Failed"));
            } finally {
                storage.dispatcher().finished(this);
            }
        }
    }


}
