package com.lpzahd.essay.db.note;

import com.lpzahd.essay.db.file.Image;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class NoteTypes extends RealmObject {

    /**
     * 记本类型
     */
    @PrimaryKey
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片地址
     */
    private Image image;

    /**
     * 位置
     */
    private int position;

    /**
     * 附属字段
     */
    private String tag;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
