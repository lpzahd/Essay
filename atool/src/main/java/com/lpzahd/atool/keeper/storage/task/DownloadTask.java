package com.lpzahd.atool.keeper.storage.task;

import com.lpzahd.atool.keeper.storage.Request;
import com.lpzahd.atool.keeper.storage.Response;
import com.lpzahd.atool.keeper.storage.Storage;
import com.lpzahd.atool.keeper.storage.internal.CallBack;
import com.lpzahd.atool.keeper.storage.internal.Progress;
import com.lpzahd.atool.keeper.storage.internal.ProgressDao;

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
    private final Request request;

    private Progress progress;

    private RealDownloadTask task;
    private CallBack callBack;

    private ProgressDao dao;

    public DownloadTask(Storage storage, Request request) {
        this.storage = storage;
        this.request = request;
        progress = new Progress();
        progress.tag = request.getTag();
        dao = request.getDao();
    }

    @Override
    public Request config() {
        return request;
    }

    @Override
    public Progress progress() {
        return progress;
    }

    @Override
    public ProgressDao dao() {
        return dao;
    }

    @Override
    public Response execute() throws IOException {
        synchronized (this) {
            if(progress.status != Progress.Status.NONE)
                throw new IllegalStateException("Already Executed");
        }

        try {
            storage.dispatcher().executed(this);
            Response response = getFileWithInterceptorConvert(null);
            if (response == null) throw new IOException("Failed");
            return response;
        } finally {
            storage.dispatcher().finished(this);
        }
    }

    @Override
    public void enqueue(CallBack callBack) {
        synchronized (this) {
            if(progress.status != Progress.Status.NONE)
                throw new IllegalStateException("Already Executed");
        }

        this.callBack = callBack;
        storage.dispatcher().enqueue(new DownloadTask.AsyncTask());
    }

    @Override
    public void pause() {
        progress.status = Progress.Status.PAUSE;
    }

    @Override
    public boolean isPause() {
        return progress.status == Progress.Status.PAUSE;
    }

    @Override
    public void resume() {
        progress.status = Progress.Status.NONE;
        enqueue(callBack);
    }

    @Override
    public void restore(Progress progress, CallBack callBack) {
        this.progress = progress;
        this.callBack = callBack;
        enqueue(callBack);
    }

    @Override
    public void cancel() {
        progress.status = Progress.Status.CANCEL;
    }

    @Override
    public boolean isExecuted() {
        return progress.status != Progress.Status.NONE;
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
        return new DownloadTask(storage, request);
    }

    private Response getFileWithInterceptorConvert(CallBack callBack) throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(storage.interceptors());

        task = new RealDownloadTask(this, interceptors, callBack);
        return task.exec();
    }

    final class AsyncTask extends NamedRunnable {

        AsyncTask() {
            super("OkHttp %s", request.getTask().getUrl());
        }

        Request request() {
            return request;
        }

        String name() {
            return request.getTask().getUrl();
        }


        DownloadTask get() {
            return DownloadTask.this;
        }

        @Override protected void execute() {
            if(callBack != null) callBack.onStart(DownloadTask.this);

            try {
                Response response = getFileWithInterceptorConvert(callBack);
                if(callBack != null) {
                    if (response == null) {
                        callBack.onFailure(DownloadTask.this, new IOException("Failed"));
                    } else {
                        callBack.onSuccess(DownloadTask.this, response);
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
