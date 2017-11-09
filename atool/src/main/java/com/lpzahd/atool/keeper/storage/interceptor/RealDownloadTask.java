package com.lpzahd.atool.keeper.storage.interceptor;

import android.support.annotation.NonNull;

import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.atool.io.IO;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.keeper.storage.CallBack;
import com.lpzahd.atool.keeper.storage.Config;
import com.lpzahd.atool.keeper.storage.Interceptor;
import com.lpzahd.atool.keeper.storage.Progress;
import com.lpzahd.atool.keeper.storage.Result;
import com.lpzahd.atool.keeper.storage.Task;
import com.lpzahd.atool.ui.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class RealDownloadTask {

    // 小文件尺寸 10M
    private static final long SIZE_MINI_FILE = 10 * 1024 * 1024 * 8;

    private final Task task;
    private final Progress progress;

    private List<Interceptor> interceptors;
    private CallBack callBack;

    public RealDownloadTask(Task task, Progress progress, List<Interceptor> interceptors, CallBack callBack) {
        this.task = task;
        this.progress = progress;
        this.interceptors = interceptors;
        this.callBack = callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public Result exec() {

        Config config = task.config();

        Config.SingleTask[] tasks = config.getTasks();

        File[] files;
        if (tasks.length == 1) {
            // 单个文件下载
            files = new File[] {
                    downloadUrl(tasks[0], progress)
            };
        } else {
            // 多文件下载 [串行下载, 并行下载待定]
            files = downloadUrls(tasks, progress);
        }

        return new Result.Builder()
                .config(config)
                .files(files)
                .progresses(progress)
                .build();
    }

    /**
     * 下载单个文件
     */
    private File downloadUrl(Config.SingleTask singleTask, Progress progress) {
        OkHttpClient client = new OkHttpClient();

        final String url = singleTask.getUrl();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "identity")
                .addHeader("referer", getHost(url))
                .build();

        if(Lists.empty(interceptors)) {
            for(Interceptor interceptor : interceptors) {
                interceptor.intercept(0, singleTask, request);
            }
        }

        Call call = client.newCall(request);

        InputStream bodyStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            Response response = call.execute();

            ResponseBody body = response.body();
            if (body == null)
                return null;

            String fileName = singleTask.getName();
            if (Strings.empty(fileName))
                fileName = getNetFileName(response, url);

            String folder = singleTask.getFolder();
            if (Strings.empty(folder))
                folder = getDefaultFolder(fileName);

            File file = new File(folder, fileName);
            if (file.exists()) {
                if(singleTask.isReplace()) {
                    boolean success = file.delete();
                    if(!success) throw new IllegalStateException("该文件删除失败");
                } else {
                    throw new IllegalStateException("该文件已存在");
                }
            }

            long contentLength = body.contentLength();

            boolean isCountType = false;
            if (contentLength < SIZE_MINI_FILE) {
                // 没有获取到,按文件数量处理 或者 文件比较小，进度则按数量来处理
                progress.totalSize = 1L;
                isCountType = true;
            }

            bodyStream = body.byteStream();
            byte[] buffer = new byte[8192];

            int len;
            fileOutputStream = new FileOutputStream(file);

            progress.status = Progress.Status.LOADING;

            if (isCountType) {

                while ((len = bodyStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.flush();


            } else {

                while ((len = bodyStream.read(buffer)) != -1 && progress.status == Progress.Status.LOADING) {
                    fileOutputStream.write(buffer, 0, len);

                    boolean write = Progress.write(progress, len, progress.totalSize);
                    if (write && callBack != null) {
                        callBack.onProgress(task, progress);
                    }
                }
            }

            if(progress.status == Progress.Status.CANCEL) {
                throw new IllegalStateException("Canceled!");
            }

            if (progress.status == Progress.Status.LOADING) {
                progress.status = Progress.Status.SUCCESS;
            }

            return file;

        } catch (IOException e) {
            L.e(e);
            progress.status = Progress.Status.FAIL;
            return null;
        } finally {
            IO.closeQuietly(bodyStream);
            IO.closeQuietly(fileOutputStream);
        }
    }

    /**
     * 串行下载
     */
    private File[] downloadUrls(Config.SingleTask[] tasks, Progress progress) {
        OkHttpClient client = new OkHttpClient();

        File[] files = new File[tasks.length];

        progress.totalSize = tasks.length;

        progress.status = Progress.Status.LOADING;

        for (Config.SingleTask config : tasks) {
            String url = config.getUrl();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("referer", getHost(url))
                    .build();

            if(Lists.empty(interceptors)) {
                for (int i = 0, size = interceptors.size(); i < size; i++) {
                    interceptors.get(i).intercept(i, config, request);
                }
            }

            Call call = client.newCall(request);

            InputStream bodyStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                Response response = call.execute();

                ResponseBody body = response.body();
                if (body == null)
                    return null;


                String fileName = config.getName();
                if (Strings.empty(fileName))
                    fileName = getNetFileName(response, url);

                String folder = config.getFolder();
                if (Strings.empty(folder))
                    folder = getDefaultFolder(fileName);

                File file = new File(folder, fileName);
                if (file.exists()) {
                    if(config.isReplace()) {
                        boolean success = file.delete();
                        if(!success) continue;
                    } else {
                        continue;
                    }
                }


                bodyStream = body.byteStream();
                byte[] buffer = new byte[8192];

                int len;
                fileOutputStream = new FileOutputStream(file);

                while ((len = bodyStream.read(buffer)) != -1 && progress.status == Progress.Status.LOADING) {
                    fileOutputStream.write(buffer, 0, len);
                }

                fileOutputStream.flush();

                boolean write = Progress.write(progress, 1, progress.totalSize);

                if (write && callBack != null) {
                    callBack.onProgress(task, progress);
                }

                if(progress.status == Progress.Status.CANCEL) {
                    throw new IllegalStateException("Canceled!");
                }

            } catch (IOException e) {
                L.e(e);
                progress.status = Progress.Status.FAIL;
                return null;
            } finally {
                IO.closeQuietly(bodyStream);
                IO.closeQuietly(fileOutputStream);
            }

        }

        if (progress.status == Progress.Status.LOADING) {
            progress.status = Progress.Status.SUCCESS;
        }

        return files;
    }

    public static class FutureResult {
        public File file;
        public Exception exc;

        public boolean isSuccess() {
            return file != null && exc == null;
        }
    }

    public interface Future {
        void get(FutureResult result);
    }

    public static File getDefaultFileName(String name) {
        return new File(getDefaultFolder(name), name);
    }

    public static void getDefaultFileName(final String url, final Future future) {
        if (Strings.empty(url) || future == null)
            throw new AssertionError("二逼不解释！");

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "identity")
                .addHeader("referer", getHost(url))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                FutureResult result = new FutureResult();
                result.exc = new Exception(e);
                future.get(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String fileName = getNetFileName(response, url);
                String folder = getDefaultFolder(fileName);
                FutureResult result = new FutureResult();
                result.file = new File(folder, fileName);
                future.get(result);
            }
        });

    }


    private static String getDefaultFolder(String fileName) {
        Files files = Keeper.getF();

        String mimeType = getMimeType(fileName);

        if (isPicture(mimeType)) {
            return files.getScopePath(Files.Scope.PHOTO_RAW);
        } else if (isVideo(mimeType)) {
            return files.getScopePath(Files.Scope.VIDEO_RAW);
        } else {
            return files.getScopePath(Files.Scope.FILE_RAW);
        }
    }

    private static boolean isPicture(String mimeType) {
        String[] pictures = new String[]{
                "jpg", "bmp", "eps", "gif", "mif",
                "miff", "png", "tif", "tiff", "svg",
                "wmf", "jpe", "jpeg", "dib", "ico",
                "tga", "cut", "pic", "webp"
        };

        for (String type : pictures) {
            if (type.equalsIgnoreCase(mimeType))
                return true;
        }
        return false;
    }

    private static boolean isVideo(String mimeType) {
        String[] pictures = new String[]{
                "mp3", "mp4", "flv", "avi", "rm",
                "rmvb", "wmv", "3gp", "mkv"
        };

        for (String type : pictures) {
            if (type.equalsIgnoreCase(mimeType))
                return true;
        }
        return false;
    }

    public static String getMimeType(String fileName) {
        if (fileName == null) return "";

        int index = fileName.lastIndexOf(".");
        return index == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private static String getHost(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url != null)
            return url.getProtocol() + "://" + url.getHost();

        return "";
    }

    private static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (Strings.empty(fileName)) fileName = getUrlFileName(url);
        if (Strings.empty(fileName)) fileName = "lpz_" + System.currentTimeMillis();

        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            L.e(e);
        }
        return fileName;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replaceAll("\"", "");
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
            split = "filename*=";
            indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                String encode = "UTF-8''";
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length(), fileName.length());
                }
                return fileName;
            }
        }
        return null;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }
}
