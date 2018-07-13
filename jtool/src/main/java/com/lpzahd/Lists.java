package com.lpzahd;

import com.lpzahd.base.NoInstance;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

    public static boolean empty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean removeIf(List<E> list, Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = list.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

    public interface Predicate<T> {
        boolean test(T var1);

        default Predicate<T> and(Predicate<? super T> var1) {
            Objects.requireNonNull(var1);
            return (var2) -> {
                return this.test(var2) && var1.test(var2);
            };
        }

        default Predicate<T> negate() {
            return (var1) -> {
                return !this.test(var1);
            };
        }

        default Predicate<T> or(Predicate<? super T> var1) {
            Objects.requireNonNull(var1);
            return (var2) -> {
                return this.test(var2) || var1.test(var2);
            };
        }

    }

}
