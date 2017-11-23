package com.lpzahd.essay.common.waiter;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.lpzahd.Strings;
import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.atool.keeper.storage.internal.CallBack;
import com.lpzahd.atool.keeper.storage.Request;
import com.lpzahd.atool.keeper.storage.internal.Progress;
import com.lpzahd.atool.keeper.storage.Response;
import com.lpzahd.atool.keeper.storage.Storage;
import com.lpzahd.atool.keeper.storage.task.Task;
import com.lpzahd.atool.keeper.storage.task.RealDownloadTask;
import com.lpzahd.atool.ui.T;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.app.App;
import com.lpzahd.fresco.zoomable.ZoomableDraweeView;
import com.lpzahd.waiter.agency.ActivityWaiter;

import java.io.File;
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
                .newTask(Request.Builder.newBuilder(url).build());

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

    public void down(String[] urls, CallBack callBack, int auto) {
        Task task = Storage.newStorage()
                .newTask(Request.Builder.newBuilder(urls).build());

        getTaskMap().put(task, auto);
        task.enqueue(callBack);
    }

    public void down(Request.SingleTask[] tasks) {
        down(tasks, defaultAppCallBack(), AUTO_NONE);
    }

    public void down(Request.SingleTask[] tasks, CallBack callBack, int auto) {
        Task task = Storage.newStorage()
                .newTask(Request.Builder.newBuilder()
                        .tasks(tasks)
                        .build());

        getTaskMap().put(task, auto);
        task.enqueue(callBack);
    }

    public void down(Request.SingleTask singleTask) {
        down(singleTask, defaultAppCallBack(), AUTO_NONE);
    }

    public void down(Request.SingleTask singleTask, CallBack callBack, int auto) {
        Task task = Storage.newStorage()
                .newTask(Request.Builder.newBuilder()
                        .task(singleTask)
                        .build());
        getTaskMap().put(task, auto);
        task.enqueue(callBack);
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
            public void onSuccess(AppCompatActivity activity, Task task, Response response) {
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
            public void runOnSuccess(Task task, Response response) {
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

        public void runOnSuccess(Task task, Response response) {

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
        final public void onSuccess(final Task task, final Response response) throws Exception {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnSuccess(task, response);
                }
            });
        }
    }

    public void downloadWithCheckFile(String url) {
        new DownLoadDialog(url).show();
    }

    private class DownLoadDialog {

        MaterialDialog dialog;

        MDButton positiveBtn;
        MDButton neutralBtn;

        View customView;

        ViewGroup progressLayout;
        ProgressBar progressBar;
        TextView contentTv;

        ViewGroup draweeLayout;
        ZoomableDraweeView zoomableDraweeView;
        TextInputLayout inputLayout;
        EditText edt;

        private String url;

        public DownLoadDialog(String url) {
            this.url = url;

            dialog = new MaterialDialog.Builder(context)
                    .title("图片下载")
                    .customView(R.layout.dialog_custom_download_progress, false)
                    .positiveText("覆盖")
                    .negativeText("取消")
                    .build();

            customView = dialog.getCustomView();

            if(customView == null)
                throw new AssertionError("customView is null!");

            positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
            positiveBtn.setEnabled(false);

            neutralBtn = dialog.getActionButton(DialogAction.NEUTRAL);

            progressLayout = (ViewGroup) customView.findViewById(R.id.progress_layout);

            progressBar = (ProgressBar) customView.findViewById(R.id.progress_bar);

            contentTv = (TextView) customView.findViewById(R.id.content_tv);
            contentTv.setText("正在校验图片是否存在...");

            draweeLayout = (ViewGroup) customView.findViewById(R.id.drawee_layout);

            zoomableDraweeView = (ZoomableDraweeView) customView.findViewById(R.id.zoomable_drawee_view);

            inputLayout = (TextInputLayout) customView.findViewById(R.id.title_input_layout);

            edt = inputLayout.getEditText();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Storage.getDefaultFileName(DownLoadDialog.this.url, future);
                }
            });
        }

        public void show() {
            dialog.show();
        }

        RealDownloadTask.Future future = new RealDownloadTask.Future() {

            @Override
            public void get(final RealDownloadTask.FutureResult result) {
                if(dialog == null || !dialog.isShowing()) return ;

                customView.post(new Runnable() {
                    @Override
                    public void run() {
                        postOnMainThread(result);
                    }
                });
            }

        };

        private void postOnMainThread(final RealDownloadTask.FutureResult result) {
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);

            if(!result.isSuccess()) {
                T.t(result.exc.getMessage());

                neutralBtn.setText("重新校验");
                neutralBtn.setEnabled(true);
                neutralBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressLayout.setVisibility(View.VISIBLE);
                        draweeLayout.setVisibility(View.GONE);

                        contentTv.setText("正在重新校验...");

                        neutralBtn.setEnabled(false);
                        positiveBtn.setText("覆盖");
                        positiveBtn.setEnabled(false);

                        Storage.getDefaultFileName(url, future);
                    }
                });

                return ;
            }

           if(!result.file.exists()) {
               contentTv.setText("该图片尚未下载！");

               neutralBtn.setVisibility(View.GONE);
               positiveBtn.setText("确定");
               positiveBtn.setEnabled(true);
               positiveBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       down(new Request.SingleTask.Builder()
                                .url(url)
                               .build());
                   }
               });
           } else {
                progressLayout.setVisibility(View.GONE);
                draweeLayout.setVisibility(View.VISIBLE);

               dialog.setTitle("发现图片");

               // 加载图片
               DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                       .setUri(Frescoer.uri(result.file.getAbsolutePath(), ImageSource.SOURCE_FILE))
                       .build();
               zoomableDraweeView.setController(draweeController);

               String name = result.file.getName();
               final String mimeType = Storage.getMimeType(name);
               if(name.length() > (6 + mimeType.length())) {
                   name = name.substring(0, 6) + "...(" + mimeType + ")";
               }
               inputLayout.setHint(name + "已存在,是否重命名？");

               // 显示重命名输入框
               showInputLayout(true, result.file);

               neutralBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       toggleInputLayout(result.file);
                   }
               });

               edt.addTextChangedListener(new SimpleTextWatcher() {
                   @Override
                   public void afterTextChanged(Editable s) {
                       if(Strings.empty(s)) {
                           positiveBtn.setEnabled(false);
                           inputLayout.setErrorEnabled(false);
                       } else {
                           File file = Storage.getDefaultFileName(s + "." + mimeType);
                           if(file.exists()) {
                               positiveBtn.setEnabled(false);
                               inputLayout.setErrorEnabled(true);
                               inputLayout.setError("该文件名已存在！");
                           } else {
                               positiveBtn.setEnabled(true);
                               inputLayout.setErrorEnabled(false);
                           }
                       }
                   }
               });

               positiveBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       if(Strings.equals(positiveBtn.getText(), "确定")) {
                           // 重命名下载
                           down(new Request.SingleTask.Builder()
                                   .url(url)
                                   .name(edt.getText() + "." + mimeType)
                                   .build());

                           dialog.dismiss();
                           return;
                       }

                       if(Strings.equals(positiveBtn.getText(), "覆盖")) {
                           // 覆盖下载
                           down(new Request.SingleTask.Builder()
                                   .url(url)
                                   .replace(true)
                                   .build());

                           dialog.dismiss();
                       }


                   }
               });
           }

        }

        private void toggleInputLayout(File file) {
            if(inputLayout.getVisibility() == View.VISIBLE) {
                showInputLayout(false, file);
            } else {
                showInputLayout(true, file);
            }

        }

        private void showInputLayout(boolean isShow, File file) {
            if(isShow) {
                inputLayout.setVisibility(View.VISIBLE);
                inputLayout.requestFocus();

                neutralBtn.setText("返回");

                positiveBtn.setText("确定");

                if(Strings.empty(edt.getText().toString())) {
                    positiveBtn.setEnabled(false);
                } else {
                    if(checkFileExit(edt.getText().toString(), Storage.getMimeType(file.getName()))) {
                        positiveBtn.setEnabled(false);
                    } else {
                        positiveBtn.setEnabled(true);
                    }
                }

            } else {
                inputLayout.setVisibility(View.GONE);

                neutralBtn.setText("重命名");
                positiveBtn.setText("覆盖");
                positiveBtn.setEnabled(true);
            }

        }

        private boolean checkFileExit(String name, String mimeType) {
            return Storage.getDefaultFileName(name + "." + mimeType).exists();
        }

    }

    private static class SimpleTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
