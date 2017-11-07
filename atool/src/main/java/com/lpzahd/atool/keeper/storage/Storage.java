package com.lpzahd.atool.keeper.storage;

import com.lpzahd.Strings;
import com.lpzahd.atool.io.IO;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

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

    public Task newTask(Config config) {
        return new DownloadTask(this, config);
    }


    public Dispatcher dispatcher() {
        return dispatcher;
    }


    public List<Interceptor> interceptors() {
        return interceptors;
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
