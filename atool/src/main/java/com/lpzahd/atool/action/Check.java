package com.lpzahd.atool.action;

import com.lpzahd.Lists;
import com.lpzahd.Objects;
import com.lpzahd.Strings;
import com.lpzahd.base.NoInstance;

import java.util.List;
import java.util.Queue;

public abstract class Check extends NoInstance {

    public interface Action {
        void onAction();
    }

    public static class Empty extends Check {

        public static boolean check(List list, Action action) {
            boolean isEmpty = Lists.empty(list);
            if(isEmpty) action.onAction();
            return isEmpty;
        }

        public static boolean check(Queue queue, Action action) {
            boolean isEmpty = Lists.empty(queue);
            if(isEmpty) action.onAction();
            return isEmpty;
        }

        public static boolean check(Object[] array, Action action) {
            boolean isEmpty = Lists.empty(array);
            if(isEmpty) action.onAction();
            return isEmpty;
        }

        public static boolean check(CharSequence str, Action action) {
            boolean isEmpty = Strings.empty(str);
            if(isEmpty) action.onAction();
            return isEmpty;
        }

        public static boolean check(Object obj, Action action) {
            boolean isEmpty = Objects.isNull(obj);
            if(isEmpty) action.onAction();
            return isEmpty;
        }
    }


}
