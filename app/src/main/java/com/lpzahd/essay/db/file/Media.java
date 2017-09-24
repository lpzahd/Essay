package com.lpzahd.essay.db.file;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Media extends RealmObject{

    /**
     * id
     */
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    /**
     * 预留字段
     */
    private String spare;

    /**
     * 媒体名字
     */
    private String name;

    /**
     * 媒体详细描述
     */
    private String subject;

    /**
     * 媒体路径
     */
    private String path;

    /**
     * 媒体使用次数
     */
    private int count = 0;

    /**
     * 媒体播放时间
     */
    private float playTime;

    /**
     * 媒体总时长
     */
    private float duration;

    /**
     * 媒体格式
     */
    private String suffix;

    /**
     * 媒体关联（联想）
     */
    private String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpare() {
        return spare;
    }

    public void setSpare(String spare) {
        this.spare = spare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPlayTime() {
        return playTime;
    }

    public void setPlayTime(float playTime) {
        this.playTime = playTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
