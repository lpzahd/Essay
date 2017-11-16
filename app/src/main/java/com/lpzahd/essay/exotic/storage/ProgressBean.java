package com.lpzahd.essay.exotic.storage;

import com.lpzahd.Lists;
import com.lpzahd.atool.keeper.storage.Progress;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 作者 : 迪
 * 时间 : 2017/11/15.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class ProgressBean extends RealmObject {

    private String tag;                              //下载的标识键

    @PrimaryKey
    private String url;                              //网址

    private String folder;                           //保存文件夹
    private String filePath;                         //保存文件地址
    private String fileName;                         //保存的文件名
    private float fraction;                          //下载的进度，0-1
    private long totalSize = 1L;                     //总字节长度, byte
    private long currentSize;                        //本次下载的大小, byte
    private int status = Progress.Status.NONE;    //当前状态
    private int priority;                            //任务优先级
    private long date;                               //创建时间
    private RealmList<BlockBean> blocks;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getFraction() {
        return fraction;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public RealmList<BlockBean> getBlocks() {
        return blocks;
    }

    public void setBlocks(RealmList<BlockBean> blocks) {
        this.blocks = blocks;
    }

    public static ProgressBean convert(Progress progress) {
        if(progress == null) return null;

        ProgressBean bean = new ProgressBean();
        bean.tag = progress.tag;
        bean.url = progress.url;
        bean.folder = progress.folder;
        bean.filePath = progress.filePath;
        bean.fileName = progress.fileName;
        bean.fraction = progress.fraction;
        bean.totalSize = progress.totalSize;
        bean.currentSize = progress.currentSize;
        bean.status = progress.status;
        bean.priority = progress.priority;
        bean.date = progress.date;

        if(progress.blocks != null) {
            RealmList<BlockBean> blockList = new RealmList<>();
            for(long val : progress.blocks) {
                BlockBean b = new BlockBean();
                b.setBlock(val);
                blockList.add(b);
            }
            bean.blocks = blockList;
        }

        return bean;
    }

    public static Progress convert(ProgressBean bean) {
        if(bean == null) return null;

        Progress progress = new Progress();
        progress.tag = bean.tag;
        progress.url = bean.url;
        progress.folder = bean.folder;
        progress.filePath = bean.filePath;
        progress.fileName = bean.fileName;
        progress.fraction = bean.fraction;
        progress.totalSize = bean.totalSize;
        progress.currentSize = bean.currentSize;
        progress.status = bean.status;
        progress.priority = bean.priority;
        progress.date = bean.date;

        if(!Lists.empty(bean.blocks)) {
            long[] blocks = new long[bean.blocks.size()];
            for (int i = 0; i < bean.blocks.size(); i++) {
                blocks[i] = bean.blocks.get(i).getBlock();
            }
            progress.blocks = blocks;
        }

        return progress;
    }

    public static void update(Progress progress, ProgressBean bean, Realm realm) {
        bean.folder = progress.folder;
        bean.filePath = progress.filePath;
        bean.fileName = progress.fileName;
        bean.fraction = progress.fraction;
        bean.totalSize = progress.totalSize;
        bean.currentSize = progress.currentSize;
        bean.status = progress.status;
        bean.priority = progress.priority;
        bean.date = progress.date;

        if(progress.blocks != null) {
            RealmList<BlockBean> blockList = new RealmList<>();
            for(long val : progress.blocks) {
                BlockBean b = realm.createObject(BlockBean.class);
                b.setBlock(val);
                blockList.add(b);
            }
            bean.blocks = blockList;
        }
    }
}
