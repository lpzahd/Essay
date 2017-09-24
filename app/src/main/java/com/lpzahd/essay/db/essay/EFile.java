package com.lpzahd.essay.db.essay;

import com.lpzahd.essay.db.file.File;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.essay.db.file.Media;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class EFile extends RealmObject {

    private RealmList<Image> images;

    private RealmList<Media> medias;

    private RealmList<File> files;

    /**
     * id
     */
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    /**
     * 预留字段
     */
    private String spare;

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public RealmList<Media> getMedias() {
        return medias;
    }

    public void setMedias(RealmList<Media> medias) {
        this.medias = medias;
    }

    public RealmList<File> getFiles() {
        return files;
    }

    public void setFiles(RealmList<File> files) {
        this.files = files;
    }

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
}
