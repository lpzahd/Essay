package com.lpzahd.atool.keeper.storage;

import android.app.Activity;

import java.io.File;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface CallBack {

    void onStart(Task task);

    // 保存在progress 状态中
//    void onPause(Task task, Progress progress);
//
//    void onResume(Task task, Progress progress);
//
//    void onCancel(Task task, Progress progress);

    void onProgress(Task task, Progress progress);

    void onFailure(Task task, Exception e);

    void onSuccess(Task task, Result result) throws Exception;

    class SimpleCallBack<T extends Activity> implements CallBack {

        final T activity;

        public SimpleCallBack(T activity) {
            this.activity = activity;
        }

        @Override
        final public void onStart(final Task task) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onStart(activity, task);
                }
            });
        }

        public void onStart(T activity, Task task) {

        }

        @Override
        final public void onProgress(final Task task, final Progress progress) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onProgress(activity, task, progress);
                }
            });
        }

        public void onProgress(T activity, Task task, Progress progress) {

        }

        @Override
        final public void onFailure(final Task task, final Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onFailure(activity, task, e);
                }
            });
        }

        public void onFailure(T activity, Task task, Exception e) {

        }

        @Override
        final public void onSuccess(final Task task, final Result result) throws Exception {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onSuccess(activity, task, result);
                }
            });
        }

        public void onSuccess(T activity, Task task, Result result) {

        }
    }
}
