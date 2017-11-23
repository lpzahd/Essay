package com.lpzahd.atool.keeper.storage;

import com.lpzahd.atool.keeper.storage.task.Interceptor;
import com.lpzahd.atool.keeper.storage.task.RealDownloadTask;
import com.lpzahd.atool.keeper.storage.task.Dispatcher;
import com.lpzahd.atool.keeper.storage.task.DownloadTask;
import com.lpzahd.atool.keeper.storage.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 作者 : 迪
 * 时间 : 2017/11/5.
 * 描述 ： 命里有时终须有，命里无时莫强求
 * 文件存储：下载，上传，对存储文件的一些操作
 *
 * 限速以后考虑
 */
public class Storage {

    // 刷新间隔时间
    public static final long PROGRESS_REFRESH_TIME = 300;

    private final Dispatcher dispatcher;
    private final List<Interceptor> interceptors;

    public static Storage newStorage() {
        return new Storage();
    }

    public Storage() {
        this(new Builder());
    }

    private Storage(Builder builder) {
        dispatcher = builder.dispatcher;
        interceptors = builder.interceptors;
    }

    public Task newTask(Request request) {
        return new DownloadTask(this, request);
    }


    public Dispatcher dispatcher() {
        return dispatcher;
    }


    public List<Interceptor> interceptors() {
        return interceptors;
    }

    public static File getDefaultFileName(String name) {
        return RealDownloadTask.getDefaultFileName(name);
    }

    public static void getDefaultFileName(String url, RealDownloadTask.Future future) {
        RealDownloadTask.getDefaultFileName(url, future);
    }

    public static String getMimeType(String fileName) {
        return RealDownloadTask.getMimeType(fileName);
    }

    public static final class Builder {

        private Dispatcher dispatcher;
        private List<Interceptor> interceptors = new ArrayList<>();

        public Builder() {
            dispatcher = new Dispatcher();
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public Storage build() {
            return new Storage(this);
        }
    }
}
