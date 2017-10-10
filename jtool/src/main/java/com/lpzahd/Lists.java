package com.lpzahd;

import com.lpzahd.base.NoInstance;

import java.util.List;
import java.util.Queue;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class Lists extends NoInstance {

    public static boolean empty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean empty(Queue queue) {
        return queue == null || queue.isEmpty();
    }
}
