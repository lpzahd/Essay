package com.lpzahd.atool.keeper.storage.task;

import android.support.annotation.Nullable;

import com.lpzahd.atool.keeper.storage.task.DownloadTask;
import com.lpzahd.atool.keeper.storage.task.Task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public final class Dispatcher {

    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private @Nullable Runnable idleCallback;

    /** Executes calls. Created lazily. */
    private @Nullable ExecutorService executorService;

    /** Ready async calls in the order they'll be run. */
    private final Deque<DownloadTask.AsyncTask> readyAsyncTasks = new ArrayDeque<>();

    /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<DownloadTask.AsyncTask> runningAsyncTasks = new ArrayDeque<>();

    /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<DownloadTask> runningSyncTasks = new ArrayDeque<>();

    public Dispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Dispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), Util.threadFactory("lpz Dispatcher", false));
        }
        return executorService;
    }

    /**
     * Set the maximum number of requests to execute concurrently. Above this requests queue in
     * memory, waiting for the running calls to complete.
     *
     * <p>If more than {@code maxRequests} requests are in flight when this is invoked, those requests
     * will remain in flight.
     */
    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        this.maxRequests = maxRequests;
        promoteCalls();
    }

    public synchronized int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Set the maximum number of requests for each host to execute concurrently. This limits requests
     * by the URL's host name. Note that concurrent requests to a single IP address may still exceed
     * this limit: multiple hostnames may share an IP address or be routed through the same HTTP
     * proxy.
     *
     * <p>If more than {@code maxRequestsPerHost} requests are in flight when this is invoked, those
     * requests will remain in flight.
     */
    public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
        if (maxRequestsPerHost < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
        }
        this.maxRequestsPerHost = maxRequestsPerHost;
        promoteCalls();
    }

    public synchronized int getMaxRequestsPerHost() {
        return maxRequestsPerHost;
    }

    /**
     * Set a callback to be invoked each time the dispatcher becomes idle (when the number of running
     * calls returns to zero).
     *
     * <p>Note: The time at which a {@linkplain Call call} is considered idle is different depending
     * on whether it was run {@linkplain Call#enqueue(Callback) asynchronously} or
     * {@linkplain Call#execute() synchronously}. Asynchronous calls become idle after the
     * {@link Callback#onResponse onResponse} or {@link Callback#onFailure onFailure} callback has
     * returned. Synchronous calls become idle once {@link Call#execute() execute()} returns. This
     * means that if you are doing synchronous calls the network layer will not truly be idle until
     * every returned {@link Response} has been closed.
     */
    public synchronized void setIdleCallback(@Nullable Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }

    synchronized void enqueue(DownloadTask.AsyncTask task) {
        if (runningAsyncTasks.size() < maxRequests && runningCallsForHost(task) < maxRequestsPerHost) {
            runningAsyncTasks.add(task);
            executorService().execute(task);
        } else {
            readyAsyncTasks.add(task);
        }
    }

    /**
     * Cancel all calls currently enqueued or executing. Includes calls executed both {@linkplain
     * Call#execute() synchronously} and {@linkplain Call#enqueue asynchronously}.
     */
    public synchronized void cancelAll() {
        for (DownloadTask.AsyncTask task : readyAsyncTasks) {
            task.get().cancel();
        }

        for (DownloadTask.AsyncTask task : runningAsyncTasks) {
            task.get().cancel();
        }

        for (DownloadTask task : runningSyncTasks) {
            task.cancel();
        }
    }

    private void promoteCalls() {
        if (runningAsyncTasks.size() >= maxRequests) return; // Already running max capacity.
        if (readyAsyncTasks.isEmpty()) return; // No ready calls to promote.

        for (Iterator<DownloadTask.AsyncTask> i = readyAsyncTasks.iterator(); i.hasNext(); ) {
            DownloadTask.AsyncTask task = i.next();

            if (runningCallsForHost(task) < maxRequestsPerHost) {
                i.remove();
                runningAsyncTasks.add(task);
                executorService().execute(task);
            }

            if (runningAsyncTasks.size() >= maxRequests) return; // Reached max capacity.
        }
    }

    /** Returns the number of running calls that share a host with {@code call}. */
    private int runningCallsForHost(DownloadTask.AsyncTask task) {
        int result = 0;
        for (DownloadTask.AsyncTask t : runningAsyncTasks) {
            if (t.name().equals(task.name())) result++;
        }
        return result;
    }

    /** Used by {@code Call#execute} to signal it is in-flight. */
    synchronized void executed(DownloadTask task) {
        runningSyncTasks.add(task);
    }

    /** Used by {@code AsyncCall#run} to signal completion. */
    void finished(DownloadTask.AsyncTask task) {
        finished(runningAsyncTasks, task, true);
    }

    /** Used by {@code Call#execute} to signal completion. */
    void finished(DownloadTask task) {
        finished(runningSyncTasks, task, false);
    }

    private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
        int runningCallsCount;
        Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
            if (promoteCalls) promoteCalls();
            runningCallsCount = runningCallsCount();
            idleCallback = this.idleCallback;
        }

        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    /** Returns a snapshot of the calls currently awaiting execution. */
    public synchronized List<Task> queuedTasks() {
        List<Task> result = new ArrayList<>();
        for (DownloadTask.AsyncTask asyncTask : readyAsyncTasks) {
            result.add(asyncTask.get());
        }
        return Collections.unmodifiableList(result);
    }

    /** Returns a snapshot of the calls currently being executed. */
    public synchronized List<Task> runningTasks() {
        List<Task> result = new ArrayList<>();
        result.addAll(runningSyncTasks);
        for (DownloadTask.AsyncTask asyncTask : runningAsyncTasks) {
            result.add(asyncTask.get());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized int queuedCallsCount() {
        return readyAsyncTasks.size();
    }

    public synchronized int runningCallsCount() {
        return runningAsyncTasks.size() + runningSyncTasks.size();
    }
}

