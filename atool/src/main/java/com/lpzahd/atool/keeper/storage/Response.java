package com.lpzahd.atool.keeper.storage;

import com.lpzahd.atool.keeper.storage.internal.Progress;

import java.io.File;

/**
 * 作者 : 迪
 * 时间 : 2017/11/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Response {

    private final Request request;
    private final File[] files;
    private final Progress[] progresses;

    private Response(Builder builder) {
        request = builder.request;
        files = builder.files;
        progresses = builder.progresses;
    }

    public boolean single() {
        return files != null && files.length == 1;
    }

    public File getFile() {
        return files[0];
    }

    public File[] getFiles() {
        return files;
    }

    public Request getRequest() {
        return request;
    }

    public static final class Builder {
        private Request request;
        private File[] files;
        private Progress[] progresses;

        public Builder() {
        }

        public Builder config(Request request) {
            this.request = request;
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

        public Response build() {
            return new Response(this);
        }
    }
}
