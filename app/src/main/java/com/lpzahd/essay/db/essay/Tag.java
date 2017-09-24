package com.lpzahd.essay.db.essay;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Lpzahd on 2016/10/13.
 */

public class Tag extends RealmObject {

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
     * 标签
     */
    private String tag;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
