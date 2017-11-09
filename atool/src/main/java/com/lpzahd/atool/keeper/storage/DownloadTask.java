package com.lpzahd.atool.keeper.storage;

import com.lpzahd.Strings;
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

    private RealDownloadTask task;
    private CallBack callBack;

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

        this.callBack = callBack;
        storage.dispatcher().enqueue(new DownloadTask.AsyncTask());
    }

    @Override
    public void cancel() {
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
    public void removeCallBack() {
        callBack = null;
        if(task != null) task.setCallBack(null);
    }

    @Override
    public Task clone() {
        return null;
    }

    private Result getFileWithInterceptorConvert(CallBack callBack) throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(storage.interceptors());

        task = new RealDownloadTask(this, progress, interceptors, callBack);
        return task.exec();
    }

    final class AsyncTask extends NamedRunnable {

        AsyncTask() {
            super("OkHttp %s", config.getTask().getUrl());
        }

        Config config() {
            return config;
        }

        String name() {
            return config.getTask().getUrl();
        }


        DownloadTask get() {
            return DownloadTask.this;
        }

        @Override protected void execute() {
            if(callBack != null) callBack.onStart(DownloadTask.this);

            try {
                Result result = getFileWithInterceptorConvert(callBack);
                if(callBack != null) {
                    if (result == null) {
                        callBack.onFailure(DownloadTask.this, new IOException("Failed"));
                    } else {
                        callBack.onSuccess(DownloadTask.this, result);
                    }
                }

            } catch (Exception e) {
                if(callBack != null) callBack.onFailure(DownloadTask.this, e);
            } finally {
                storage.dispatcher().finished(this);
            }
        }
    }


}
