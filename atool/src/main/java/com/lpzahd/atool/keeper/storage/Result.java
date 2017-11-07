package com.lpzahd.atool.keeper.storage;

import java.io.File;

import okhttp3.Response;

/**
 * 作者 : 迪
 * 时间 : 2017/11/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Result {

    private final Config config;
    private final File[] files;
    private final Progress[] progresses;

    private Result(Builder builder) {
        config = builder.config;
        files = builder.files;
        progresses = builder.progresses;
    }

    public File getFile() {
        return files[0];
    }

    public File[] getFiles() {
        return files;
    }

    public Config getConfig() {
        return config;
    }

    public static final class Builder {
        private Config config;
        private File[] files;
        private Progress[] progresses;

        public Builder() {
        }

        public Builder config(Config config) {
            this.config = config;
            return this;
        }

        public Builder files(File... files) {
            this.files = files;
            return this;
        }

        public Builder progresses(Progress... progresses) {
            this.progresses = progresses;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}
