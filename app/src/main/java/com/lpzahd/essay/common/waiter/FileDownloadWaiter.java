package com.lpzahd.essay.common.waiter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lpzahd.Strings;
import com.lpzahd.atool.keeper.storage.CallBack;
import com.lpzahd.atool.keeper.storage.Config;
import com.lpzahd.atool.keeper.storage.Progress;
import com.lpzahd.atool.keeper.storage.Result;
import com.lpzahd.atool.keeper.storage.Storage;
import com.lpzahd.atool.keeper.storage.Task;
import com.lpzahd.atool.ui.T;
import com.lpzahd.essay.R;
import com.lpzahd.essay.app.App;
import com.lpzahd.waiter.agency.ActivityWaiter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 作者 : 迪
 * 时间 : 2017/11/8.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class FileDownloadWaiter extends ActivityWaiter<AppCompatActivity, ActivityWaiter>{

    public static final int AUTO_NONE = 0x00;
    public static final int AUTO_CANCEL = 0x01;
    public static final int AUTO_REMOVE_CALLBACK = 0x10;

    private Map<Task, Integer> taskMap;

    public FileDownloadWaiter(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    private Map<Task, Integer> getTaskMap() {
        return (taskMap == null) ? (taskMap = new HashMap<>()) : taskMap;
    }

    public void downDefault(String url) {
        down(url, defaultAppCallBack(), AUTO_NONE);
    }

    public void down(String url) {
        down(url, null, AUTO_NONE);
    }

    public void down(String url, CallBack callBack) {
        down(url, callBack, AUTO_NONE);
    }

    public void down(String url, CallBack callBack, int auto) {
        Task task = Storage.newStorage()
                .newTask(Config.Builder.newBuilder(url).build());

        getTaskMap().put(task, auto);
        task.enqueue(callBack);
    }

    public void downDefault(String... urls) {
        down(urls, defaultAppCallBack(), AUTO_NONE);
    }


    public void down(String... urls) {
        down(urls, null, AUTO_NONE);
    }

    public void down(String[] urls, CallBack callBack) {
        down(urls, callBack, AUTO_NONE);
    }

    public static void down(String[] urls, CallBack callBack, int auto) {
        Storage.newStorage()
                .newTask(Config.Builder.newBuilder(urls).build())
                .enqueue(callBack);
    }

    public void showDownLoadDialog(final String url) {
        showDownLoadDialog(null, url);
    }

    public void showDownLoadDialog(String name, final String url) {
        if(Strings.empty(name)) name = url.substring(url.lastIndexOf("/") + 1).trim();
        new MaterialDialog.Builder(context)
                .title("图片下载")
                .content(name)
                .positiveText(R.string.tip_positive)
                .negativeText(R.string.tip_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        downDefault(url);
                    }
                })
                .show();
    }

    public void showDownLoadDialog(final String... urls) {
        showDownLoadDialog(null, urls);
    }

    public void showDownLoadDialog(String name, final String... urls) {
        if(Strings.empty(name)) name = "你确定要批量下载这些图片么?";
        new MaterialDialog.Builder(context)
                .title("图片下载")
                .content(name)
                .positiveText(R.string.tip_positive)
                .negativeText(R.string.tip_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        downDefault(urls);
                    }
                })
                .show();
    }

    @Override
    protected void destroy() {
        super.destroy();

        Map<Task, Integer> tasks = getTaskMap();
        Set<Map.Entry<Task, Integer>> entrySet = tasks.entrySet();
        for (Map.Entry<Task, Integer> entry : entrySet) {
            Integer auto = entry.getValue();

            if(auto == AUTO_NONE) {
                continue;
            }

            if((auto & AUTO_CANCEL) == AUTO_CANCEL) {
                entry.getKey().cancel();
            }

            if((auto & AUTO_REMOVE_CALLBACK) == AUTO_REMOVE_CALLBACK) {
                entry.getKey().removeCallBack();
            }
        }
        tasks.clear();
    }

    private CallBack defaultActivityCallBack() {
        return new CallBack.SimpleCallBack<AppCompatActivity>(context) {

            @Override
            public void onStart(AppCompatActivity activity, Task task) {
                T.t("图片开始下载");
            }

            @Override
            public void onFailure(AppCompatActivity activity, Task task, Exception e) {
                T.t("图片下载失败");
            }

            @Override
            public void onSuccess(AppCompatActivity activity, Task task, Result result) {
                T.t("图片下载完成");
            }

        };
    }

    private CallBack defaultAppCallBack() {
        return new HandlerCallBack(App.getApp().getHandler()) {

            @Override
            public void runOnStart(Task task) {
                T.t("图片开始下载");
            }

            @Override
            public void runOnFailure(Task task, Exception e) {
                T.t("图片下载失败");
            }

            @Override
            public void runOnSuccess(Task task, Result result) {
                T.t("图片下载完成");
            }

        };
    }

    private static class HandlerCallBack implements CallBack {

        private Handler handler;

        public HandlerCallBack(Handler handler) {
            this.handler = handler;
        }

        public void runOnStart(Task task) {

        }

        public void runOnProgress(Task task, Progress progress) {

        }

        public void runOnFailure(Task task, Exception e) {

        }

        public void runOnSuccess(Task task, Result result) {

        }

        @Override
        final public void onStart(final Task task) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnStart(task);
                }
            });
        }

        @Override
        final public void onProgress(final Task task, final Progress progress) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnProgress(task, progress);
                }
            });
        }

        @Override
        final public void onFailure(final Task task, final Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnFailure(task, e);
                }
            });
        }

        @Override
        final public void onSuccess(final Task task, final Result result) throws Exception {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnSuccess(task, result);
                }
            });
        }
    }
}
