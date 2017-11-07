package com.lpzahd.atool.keeper.storage;

import java.io.File;
import java.io.IOException;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface Task extends Cloneable {

    Config config();

    Result execute() throws IOException;

    void enqueue(CallBack responseCallback);

    void cancel();

    boolean isExecuted();

    boolean isCanceled();

    Task clone();

    interface Factory {
        Task newTask(Config config);
    }
}
