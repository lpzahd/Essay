package com.lpzahd.essay.db.leisure;

import org.threeten.bp.Instant;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 迪 on 2016/10/30.
 */

public class WordQuery extends RealmObject {

    /**
     * 预留字段
     */
    public String spare;

    /**
     * 创建时间
     */
    public long date = Instant.now().toEpochMilli();

    /**
     * 查询关键字
     */
    @PrimaryKey
    public String word;

    /**
     * 查询次数
     */
    public int count;

}
