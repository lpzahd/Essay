package com.lpzahd.atool.keeper.storage.internal;

import com.lpzahd.Strings;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public abstract class NamedRunnable implements Runnable {
    protected final String name;

    public NamedRunnable(String format, Object... args) {
        this.name = Strings.format(format, args);
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    protected abstract void execute();
}
