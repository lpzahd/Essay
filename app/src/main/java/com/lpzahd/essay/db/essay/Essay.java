package com.lpzahd.essay.db.essay;

import com.lpzahd.Lists;
import com.lpzahd.essay.db.file.Image;

import org.threeten.bp.Instant;

import java.util.Collections;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Essay extends RealmObject {

//    public Essay(String content) {
//        this.content = content;
//    }
//
//    public Essay(String title, String content) {
//        this.title = title;
//        this.content = content;
//    }
//
//    public Essay(String title, String content, Image... images) {
//        this.title = title;
//        this.content = content;
//
//        if(!Lists.empty(images)) {
//            eFile = new EFile();
//
//            RealmList<Image> imageRealmList = new RealmList<>();
//            Collections.addAll(imageRealmList, images);
//            eFile.setImages(imageRealmList);
//        }
//    }

    public void setImages(Image... images) {
        if (eFile == null)
            eFile = new EFile();

        addImages(new RealmList<Image>(), images);
    }

    public void addImages(Image... images) {
        if (eFile == null)
            eFile = new EFile();

        if (Lists.empty(eFile.getImages())) {
            addImages(new RealmList<Image>(), images);
        } else {
            addImages(eFile.getImages(), images);
        }
    }

    private void addImages(RealmList<Image> imageRealmList, Image... images) {
        Collections.addAll(imageRealmList, images);
        eFile.setImages(imageRealmList);
    }

    public void setImages(RealmList<Image> images) {
        if (eFile == null)
            eFile = new EFile();

        eFile.setImages(images);
    }

    public RealmList<Image> getDefaultImages() {
        if (eFile == null || eFile.getImages() == null)
            return new RealmList<>();
        return eFile.getImages();
    }

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
     * 标题
     */
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 是否显示
     */
    private boolean isShow = true;

    /**
     * 文件集
     */
    private EFile eFile;

    /**
     * 标签集
     */
    private RealmList<Tag> tag;

    /**
     * 标记(特殊标记， 人品标记 1)
     */
    private byte mark;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public EFile geteFile() {
        return eFile;
    }

    public void seteFile(EFile eFile) {
        this.eFile = eFile;
    }

    public RealmList<Tag> getTag() {
        return tag;
    }

    public void setTag(RealmList<Tag> tag) {
        this.tag = tag;
    }

    public byte getMark() {
        return mark;
    }

    public void setMark(byte mark) {
        this.mark = mark;
    }
}
