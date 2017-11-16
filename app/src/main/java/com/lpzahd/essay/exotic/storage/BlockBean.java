package com.lpzahd.essay.exotic.storage;

import io.realm.RealmObject;

/**
 * 作者 : 迪
 * 时间 : 2017/11/15.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class BlockBean extends RealmObject {

    private long block;

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }
}
