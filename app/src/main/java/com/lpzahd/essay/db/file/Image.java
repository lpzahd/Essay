package com.lpzahd.essay.db.file;

import org.threeten.bp.Instant;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Image extends RealmObject {

    /**
     * uuid
     */
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    /**
     * 路径
     */
    private String path;

    /**
     * 来源
     *
     * @link Config com.lpzahd.image
     */
    private @com.lpzahd.atool.enmu.Image.SOURCE int source;

    /**
     * 宽
     */
    private int width;

    /**
     * 高
     */
    private int height;

    /**
     * 图片描述
     */
    private int desc;

    /**
     * 图片格式
     */
    private String suffix;

    /**
     * 图片关联（联想）
     */
    private String link;

    /**
     * 创建时间
     */
    private long date = Instant.now().toEpochMilli();

    /**
     * 附属字段
     */
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public @com.lpzahd.atool.enmu.Image.SOURCE int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDesc() {
        return desc;
    }

    public void setDesc(int desc) {
        this.desc = desc;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
