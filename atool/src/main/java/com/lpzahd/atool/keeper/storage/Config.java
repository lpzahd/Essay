package com.lpzahd.atool.keeper.storage;

import com.lpzahd.Lists;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Config {

    private final SingleTask[] tasks;
    private final Object tag;

    public static class SingleTask {

        private final String url;
        private final String folder;
        private final String name;
        private final boolean replace;
        private long progress;

        private SingleTask(Builder builder) {
            url = builder.url;
            folder = builder.folder;
            name = builder.name;
            replace = builder.replace;
            progress = builder.progress;
        }

        public String getUrl() {
            return url;
        }

        public String getFolder() {
            return folder;
        }

        public String getName() {
            return name;
        }

        public boolean isReplace() {
            return replace;
        }

        public long getProgress() {
            return progress;
        }

        public static final class Builder {
            private String url;
            private String folder;
            private String name;
            private boolean replace;
            private long progress;

            public Builder() {
            }

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder folder(String folder) {
                this.folder = folder;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder replace(boolean replace) {
                this.replace = replace;
                return this;
            }

            public Builder progress(long progress) {
                this.progress = progress;
                return this;
            }

            public SingleTask build() {
                return new SingleTask(this);
            }
        }
    }

    private Config(Builder builder) {
        tasks = builder.tasks;
        tag = builder.tag;
    }

    public SingleTask getTask() {
        return Lists.empty(tasks) ? null : tasks[0];
    }

    public SingleTask[] getTasks() {
        return tasks;
    }

    public Object getTag() {
        return tag;
    }

    public static final class Builder {

        private Object tag;
        private SingleTask[] tasks;

        public static Builder newBuilder(String url) {
            return new Builder()
                    .url(url);
        }

        public static Builder newBuilder(String... urls) {
            return new Builder()
                    .urls(urls);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder() {
        }


        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Config build() {
            if (Lists.empty(tasks))
                throw new NullPointerException("没有任务！");

            return new Config(this);
        }

        public Builder urls(String... urls) {
            SingleTask[] tasks = new SingleTask[urls.length];
            for (int i = 0; i < urls.length; i++) {
                tasks[i] = new SingleTask.Builder().url(urls[i])
                        .build();
            }
            this.tasks = tasks;
            return this;
        }

        public Builder url(String url) {
            SingleTask task = new SingleTask.Builder().url(url)
                    .build();
            this.tasks = new SingleTask[]{task};
            return this;
        }

        public Builder task(SingleTask task) {
            this.tasks = new SingleTask[]{task};
            return this;
        }

        public Builder tasks(SingleTask[] tasks) {
            this.tasks = tasks;
            return this;
        }
    }
}
