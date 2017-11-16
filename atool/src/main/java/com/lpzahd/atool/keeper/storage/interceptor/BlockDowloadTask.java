package com.lpzahd.atool.keeper.storage.interceptor;

import com.lpzahd.atool.io.IO;
import com.lpzahd.atool.keeper.storage.CallBack;
import com.lpzahd.atool.keeper.storage.Progress;
import com.lpzahd.atool.keeper.storage.Task;
import com.lpzahd.atool.keeper.storage.internal.ProgressDao;
import com.lpzahd.atool.ui.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者 : 迪
 * 时间 : 2017/11/10.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class BlockDowloadTask {

    // 默认线程数量
    private int threadCount = 4;

    private CallBack callBack;

    private Task task;
    private Progress progress;
    private String url;
    private long contentLength;

    private ProgressDao innerDao;

    private String filePath;

    private CountDownLatch latch;

    public BlockDowloadTask(Task task, Progress progress, String url, long contentLength, String filePath) {
        this.task = task;
        this.progress = progress;
        this.url = url;
        this.contentLength = contentLength;
        this.filePath = filePath;
        this.latch = new CountDownLatch(threadCount);

        ProgressDao dao = task.dao();
        if(dao != null)
            innerDao = dao.clone();

        if(progress.blocks == null)
            progress.newBlocks(threadCount);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public File exec() {
        try {
            File file = new File(filePath);

            if(!file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rwd");
                randomAccessFile.setLength(contentLength);

                if(innerDao != null) innerDao.insert(progress);
            }

            long averageSize = contentLength / threadCount;

            L.e("即将开始下载");
            for (int i = 0; i < threadCount; i++) {

                long start = averageSize * i;
                long end = (i == averageSize - 1) ? contentLength : start + averageSize - 1;
                L.e("线程" + i + " 之前的下载量 ： " + progress.blocks[i]);
                BlockThread thread = new BlockThread(i, start + progress.blocks[i], end);
                thread.setName("线程" + i);
                thread.start();
            }

            latch.await();

            if(progress.status == Progress.Status.LOADING) {
                L.e( "下载完成");

                if(innerDao != null) innerDao.delete(progress.url);

                return new File(filePath);
            } else {
                int total = 0;
                for (int i = 0; i < progress.blocks.length; i++) {
                    total += progress.blocks[i];
                }
                L.e("下载失败 ：" + total + " 总大小为 ： " + progress.totalSize);
                return null;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(innerDao != null) innerDao.close();
        }

        return null;

    }

    final class BlockThread extends Thread {

        private int index;
        private long start;
        private long end;
        private ProgressDao innerDao;

        public BlockThread(int index, long start, long end) {
            this.index = index;
            this.start = start;
            this.end = end;

            L.e(getName() + " 起始位置 ：" + start + "   结束位置 ：" + end);
        }

        @Override
        public void run() {
            if(task.dao() != null)
                innerDao = task.dao().clone();
            InputStream bodyStream = null;
            RandomAccessFile randomAccessFile = null;
            try {

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept-Encoding", "identity")
                        .addHeader("Range", "bytes=" + start + "-" + end)
                        .build();

                Call call = client.newCall(request);

                Response response = call.execute();

                if(!response.isSuccessful()) {
                    // 没有想到好的方式
                    // 重试？？？
                    L.e(getName() + "链接失败 : " + response.code());
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) {
                    L.e(getName() + "body 为 null");
                    return ;
                }

                bodyStream = body.byteStream();
                byte[] buffer = new byte[8192];

                int len;
                randomAccessFile = new RandomAccessFile(filePath, "rwd");
                randomAccessFile.seek(start);

                while ((len = bodyStream.read(buffer)) != -1 && progress.status == Progress.Status.LOADING) {
                    randomAccessFile.write(buffer, 0, len);
                    boolean write = Progress.write(progress, len, progress.totalSize);
                    progress.blocks[index] += len;

                    if(innerDao != null) innerDao.update(progress);

                    if(write) {
                        L.e(getName() + "成功写入 当前速度为 " + progress.speed / 1024 + "kb/s");
                    }
                    if (write && callBack != null) {
                        callBack.onProgress(task, progress);
                    }
                }

                L.e(getName() + "任务状态" + progress.status);
                L.e(getName() + "下载量" + progress.blocks[index]);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IO.closeQuietly(bodyStream);
                IO.closeQuietly(randomAccessFile);
                if(innerDao != null) innerDao.close();
                latch.countDown();
            }
        }
    }
}
