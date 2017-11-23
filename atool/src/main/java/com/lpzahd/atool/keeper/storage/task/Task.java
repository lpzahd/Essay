package com.lpzahd.atool.keeper.storage.task;

import com.lpzahd.atool.keeper.storage.Request;
import com.lpzahd.atool.keeper.storage.Response;
import com.lpzahd.atool.keeper.storage.internal.CallBack;
import com.lpzahd.atool.keeper.storage.internal.Progress;
import com.lpzahd.atool.keeper.storage.internal.ProgressDao;

import java.io.IOException;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public interface Task extends Cloneable {

    Request config();

    Progress progress();

    ProgressDao dao();

    Response execute() throws IOException;

    void enqueue(CallBack responseCallback);

    void pause();

    boolean isPause();

    void resume();

    void restore(Progress progress, CallBack callBack);

    void cancel();

    boolean isExecuted();

    boolean isCanceled();

    void removeCallBack();

    Task clone();

    interface Factory {
        Task newTask(Request request);
    }
}
