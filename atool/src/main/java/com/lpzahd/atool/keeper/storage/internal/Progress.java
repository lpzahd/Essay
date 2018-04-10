package com.lpzahd.atool.keeper.storage.internal;

import android.os.SystemClock;
import android.support.annotation.IntDef;

import com.lpzahd.atool.keeper.storage.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者 : 迪
 * 时间 : 2017/11/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Progress {

    public String tag;                              //下载的标识键
    public String url;                              //网址
    public String folder;                           //保存文件夹
    public String filePath;                         //保存文件地址
    public String fileName;                         //保存的文件名
    public float fraction;                          //下载的进度，0-1
    public long totalSize = 1L;                     //总字节长度, byte
    public long currentSize;                        //本次下载的大小, byte
    public transient long speed;                    //网速，byte/s
    public int status = Status.NONE;    //当前状态
    public int priority;                            //任务优先级
    public long date;                               //创建时间
//    public Request<?, ? extends Request> request;   //网络请求
    public Serializable extra1;                     //额外的数据
    public Serializable extra2;                     //额外的数据
    public Serializable extra3;                     //额外的数据
    public Throwable exception;                     //当前进度出现的异常

    public long[] blocks;

    private transient long tempSize;                //每一小段时间间隔的网络流量
    private transient long lastRefreshTime;         //最后一次刷新的时间
    private transient List<Long> speedBuffer;       //网速做平滑的缓存，避免抖动过快

    public void newBlocks(int len) {
        blocks = new long[len];
    }

    public Progress() {
        lastRefreshTime = SystemClock.elapsedRealtime();
        totalSize = -1;
        priority = 0;
        date = System.currentTimeMillis();
        speedBuffer = new ArrayList<>();
    }

    public static class Status {
        public static final int NONE = 0;         //无状态
        public static final int CANCEL = 1;      //取消
        public static final int LOADING = 2;      //下载中
        public static final int PAUSE = 3;        //暂停
        public static final int FAIL = 4;        //错误
        public static final int SUCCESS = 5;       //完成
    }

    @IntDef({Status.NONE, Status.CANCEL, Status.LOADING,
            Status.PAUSE,Status.FAIL,Status.SUCCESS})
    @interface TaskStatus {

    }

    public static synchronized boolean write(Progress progress, long writeSize, long totalSize) {
        progress.totalSize = totalSize;
        progress.currentSize += writeSize;
        progress.tempSize += writeSize;

        long currentTime = SystemClock.elapsedRealtime();
        boolean isNotify = (currentTime - progress.lastRefreshTime) >= Storage.PROGRESS_REFRESH_TIME;
        boolean isWrite = isNotify || progress.currentSize == totalSize;
        if (isWrite) {
            long diffTime = currentTime - progress.lastRefreshTime;
            if (diffTime == 0) diffTime = 1;
            progress.fraction = progress.currentSize * 1.0f / totalSize;
            progress.speed = progress.bufferSpeed(progress.tempSize * 1000 / diffTime);
            progress.lastRefreshTime = currentTime;
            progress.tempSize = 0;
        }

        return isWrite;
    }

    public void write(long writeSize, long totalSize) {
        Progress.write(this, writeSize, totalSize);
    }

    /** 平滑网速，避免抖动过大 */
    private long bufferSpeed(long speed) {
        speedBuffer.add(speed);
        if (speedBuffer.size() > 10) {
            speedBuffer.remove(0);
        }
        long sum = 0;
        for (float speedTemp : speedBuffer) {
            sum += speedTemp;
        }
        return sum / speedBuffer.size();
    }

    @Override
    public String toString() {
        return "Progress{" +
                ", fraction=" + fraction +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", speed=" + speed +
                ", status=" + status +
                '}';
    }
}
