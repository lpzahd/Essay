package com.lpzahd.essay.db.collection;

import com.lpzahd.essay.db.file.Image;

import org.threeten.bp.Instant;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 作者 : 迪
 * 时间 : 2018/2/16.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Collection extends RealmObject {

    /**
     * uuid
     */
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    /**
     * 预留字段
     */
    private String spare;

    /**
     * 创建时间
     */
    private long date = Instant.now().toEpochMilli();

    /**
     * 图片
     */
    private Image image;

    /**
     * 图片原地址
     */
    private String originalPath;

    /**
     * 图片验证
     */
    private String MD5;

    /**
     * 转移完成【转移操作】
     */
    private boolean trans;

    /**
     * 收藏次数【收藏备用次数-保留字段】
     */
    private int count;

    /**
     * 标签【保留字段】
     */
    private String tag;

    /**
     * 权重【用于排序】
     */
    private int weight = 1;

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public boolean isTrans() {
        return trans;
    }

    public void setTrans(boolean trans) {
        this.trans = trans;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
