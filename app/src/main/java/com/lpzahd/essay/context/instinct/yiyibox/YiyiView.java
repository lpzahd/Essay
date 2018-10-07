package com.lpzahd.essay.context.instinct.yiyibox;

/**
 * 作者 : Administrator
 * 时间 : 2018/10/7.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiView {
    /**
     * code : 200
     * data : {"uri":"https://h3.h1d9.com/640/06/06315cbcb24976283c88406fa0f93cbc5af2ca8c.mp4?md5=X45yTdp04UzZNHnMS-BgvA&expires=1538892198"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uri : https://h3.h1d9.com/640/06/06315cbcb24976283c88406fa0f93cbc5af2ca8c.mp4?md5=X45yTdp04UzZNHnMS-BgvA&expires=1538892198
         */

        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
