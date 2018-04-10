package com.lpzahd.atool.keeper.storage.task;

import android.support.annotation.NonNull;

import com.lpzahd.Lists;
import com.lpzahd.Strings;
import com.lpzahd.IO;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.keeper.storage.Request;
import com.lpzahd.atool.keeper.storage.Response;
import com.lpzahd.atool.keeper.storage.internal.CallBack;
import com.lpzahd.atool.keeper.storage.internal.Progress;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class RealDownloadTask {

    // 小文件尺寸 10M
    private static final long SIZE_MINI_FILE = 10 * 1024 * 1024;

    private final Task task;
    private final Progress progress;

    private List<Interceptor> interceptors;
    private CallBack callBack;

    public RealDownloadTask(Task task, List<Interceptor> interceptors, CallBack callBack) {
        this.task = task;
        this.progress = task.progress();
        this.interceptors = interceptors;
        this.callBack = callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public Response exec() {

        Request request = task.config();

        Request.SingleTask[] tasks = request.getTasks();

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

        return new Response.Builder()
                .config(request)
                .files(files)
                .progresses(progress)
                .build();
    }

    /**
     * 下载单个文件
     */
    private File downloadUrl(Request.SingleTask singleTask, Progress progress) {
        OkHttpClient client = new OkHttpClient();

        final String url = singleTask.getUrl();
        progress.url = url;

        okhttp3.Request request = new okhttp3.Request.Builder()
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
            okhttp3.Response response = call.execute();

            ResponseBody body = response.body();
            if (body == null)
                return null;

            String fileName = singleTask.getName();
            if (Strings.empty(fileName))
                fileName = getNetFileName(response, url);
            progress.fileName = fileName;

            String folder = singleTask.getFolder();
            if (Strings.empty(folder))
                folder = getDefaultFolder(fileName);
            progress.folder = folder;

            File file = new File(folder, fileName);
            progress.filePath = file.getAbsolutePath();

            if (file.exists()) {
                if(singleTask.isReplace()) {
                    boolean success = file.delete();
                    if(!success) throw new IllegalStateException("该文件删除失败");
                } else {
                    if(progress.currentSize == progress.totalSize) {
                        throw new IllegalStateException("该文件已存在");
                    }
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

            progress.status = Progress.Status.LOADING;

            if (isCountType) {
                fileOutputStream = new FileOutputStream(file);
                while ((len = bodyStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.flush();


            } else {

                progress.totalSize = contentLength;

                BlockDowloadTask blockDowloadTask = new BlockDowloadTask(
                        task, progress, url, contentLength, file.getAbsolutePath()
                );

                file = blockDowloadTask.exec();
            }

            if(progress.status == Progress.Status.PAUSE) {
                throw new IllegalStateException("Paused!");
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
    private File[] downloadUrls(Request.SingleTask[] tasks, Progress progress) {
        OkHttpClient client = new OkHttpClient();

        File[] files = new File[tasks.length];

        progress.totalSize = tasks.length;

        progress.status = Progress.Status.LOADING;

        for(int t = 0; t < tasks.length; t++) {
            Request.SingleTask config = tasks[t];
            String url = config.getUrl();
            progress.url = url;
            okhttp3.Request request = new okhttp3.Request.Builder()
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
                okhttp3.Response response = call.execute();

                ResponseBody body = response.body();
                if (body == null)
                    return null;


                String fileName = config.getName();
                if (Strings.empty(fileName))
                    fileName = getNetFileName(response, url);
                progress.fileName = fileName;

                String folder = config.getFolder();
                if (Strings.empty(folder))
                    folder = getDefaultFolder(fileName);
                progress.folder = folder;

                File file = new File(folder, fileName);
                progress.fileName = file.getAbsolutePath();
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

                files[t] = file;
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

        final okhttp3.Request request = new okhttp3.Request.Builder()
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
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                String fileName = getNetFileName(response, url);
                String folder = getDefaultFolder(fileName);
                FutureResult result = new FutureResult();
                result.file = new File(folder, fileName);
                future.get(result);
            }
        });

    }

    public static String getPhotoDefaultPath() {
        return Keeper.getF().getScopePath(Files.Scope.PHOTO_RAW);
    }

    public static String getVideoDefaultPath() {
        return Keeper.getF().getScopePath(Files.Scope.VIDEO_RAW);
    }

    public static String getFileDefaultPath() {
        return Keeper.getF().getScopePath(Files.Scope.FILE_RAW);
    }

    private static String getDefaultFolder(String fileName) {
        String mimeType = getMimeType(fileName);

        if (isPicture(mimeType)) {
            return getPhotoDefaultPath();
        } else if (isVideo(mimeType)) {
            return getVideoDefaultPath();
        } else {
            return getFileDefaultPath();
        }
    }

    public static boolean isPicture(String mimeType) {
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

    public static boolean isVideo(String mimeType) {
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

    private static String getNetFileName(okhttp3.Response response, String url) {
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
    private static String getHeaderFileName(okhttp3.Response response) {
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
