package com.lpzahd.atool.keeper.storage.internal;

import com.lpzahd.atool.keeper.storage.Progress;

/**
 * 作者 : 迪
 * 时间 : 2017/11/15.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface ProgressDao {

    void insert(Progress progress);

    void update(Progress progress);

    void delete(String url);

    Progress select(String url);

    void close();

    ProgressDao clone();
}
