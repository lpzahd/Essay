package com.lpzahd.atool.keeper.storage;

/**
 * 作者 : 迪
 * 时间 : 2017/11/6.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class Config {

    private final String[] urls;
    private final String name;
    private final long progress;
    private final Object tag;

    private Config(Builder builder) {
        urls = builder.urls;
        name = builder.name;
        progress = builder.progress;
        tag = builder.tag;
    }

    public String getUrl() {
        return urls[0];
    }

    public String[] getUrls() {
        return urls;
    }

    public String getName() {
        return name;
    }

    public long getProgress() {
        return progress;
    }

    public Object getTag() {
        return tag;
    }

    public static final class Builder {
        private String name;
        private long progress;
        private Object tag;
        private String[] urls;

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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder progress(long progress) {
            this.progress = progress;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Config build() {
            if(urls == null || urls.length == 0)
                 throw new NullPointerException("设置地址！");

            return new Config(this);
        }

        public Builder urls(String... urls) {
            this.urls = urls;
            return this;
        }

        public Builder url(String url) {
            urls = new String[] {
                    url
            };
            return this;
        }
    }
}
