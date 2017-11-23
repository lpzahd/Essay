package com.lpzahd.atool.keeper;

import com.lpzahd.atool.keeper.storage.internal.CallBack;
import com.lpzahd.atool.keeper.storage.Request;
import com.lpzahd.atool.keeper.storage.Storage;
import com.lpzahd.atool.keeper.storage.task.Task;

/**
 * 作者 : 迪
 * 时间 : 2017/11/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Downloads {

    private static Downloads downloads;

    private Downloads() {
    }

    static Downloads getDownload() {
        return downloads == null ? downloads = new Downloads() : downloads;
    }

    public static void down(String url) {
        down(url, null);
    }

    public static void down(String url, CallBack callBack) {
        Task task = Storage.newStorage()
                .newTask(Request.Builder.newBuilder(url).build());

        task.enqueue(callBack);
    }

    public static void down(String[] urls, CallBack callBack) {
        Storage.newStorage()
                .newTask(Request.Builder.newBuilder(urls).build())
                .enqueue(callBack);
    }

}
