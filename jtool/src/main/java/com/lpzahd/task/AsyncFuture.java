package com.lpzahd.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author lpzahd
 * @describe
 * @time 2018/3/19 9:44
 * @change
 */
public class AsyncFuture<V> implements Future<V> {

    private final Object mLocker = new Object();
    private boolean mReady = false;
    private V mObject;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        synchronized(this.mLocker) {
            return this.mReady;
        }
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        synchronized(this.mLocker) {
            this.mLocker.wait();
            return this.mObject;
        }
    }

    @Override
    public V get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized(this.mLocker) {
            this.mLocker.wait(timeUnit.toMillis(timeout));
            return this.mObject;
        }
    }

    public void setDone(V object) {
        synchronized(this.mLocker) {
            isTrue(!this.mReady);
            this.mObject = object;
            this.mReady = true;
            this.mLocker.notify();
        }
    }

    public static void isTrue(boolean condition) {
        if(!condition) {
            throw new RuntimeException();
        }
    }
}
