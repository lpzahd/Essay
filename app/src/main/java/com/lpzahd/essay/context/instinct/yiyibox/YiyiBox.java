package com.lpzahd.essay.context.leisure.yiyibox;

import java.util.List;

/**
 * 作者 : 迪
 * 时间 : 2017/10/27.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiBox {


    /**
     * code : 200
     * data : {"items":[{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":218739,"name":"WANYI","nickname":"oscarzhang","userid":390480},"follow_times":5,"height":1000,"id":666947,"img":"//i.b0b1.com/250/73/732c8df11552ddf287f9ebbf05824275fc17f306.jpg","num":2,"shorturl":"u/666947","width":666},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":192559,"name":"放荡不羁","nickname":"放荡不羁","userid":332086},"follow_times":9,"height":1280,"id":666937,"img":"//i.b0b1.com/250/cb/cb669e2793ba7fc7576b7790dbcb667e47c3f984.jpg","num":2,"shorturl":"u/666937","width":852},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":224475,"name":"欧卡丽娜","nickname":"mocka","userid":349079},"follow_times":9,"height":651,"id":666942,"img":"//i.b0b1.com/250/d6/d66d4b1461202749eb8fc89e1ed605143c2de408.jpg","num":2,"shorturl":"u/666942","width":426},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":226529,"name":"美女图片","nickname":"黑色星期五","userid":407699},"follow_times":7,"height":855,"id":666927,"img":"//i.b0b1.com/250/b0/b0e41e3037cec7d5b501e2becea56f6f75655cde.jpg","num":2,"shorturl":"u/666927","width":570},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":167777,"name":"收口交小奴","nickname":"收口交女奴","userid":264251},"follow_times":2,"height":750,"id":666928,"img":"//i.b0b1.com/250/9a/9a7b5012c8ae9c7084b2bd5229860ee121c5b6c9.jpg","num":2,"shorturl":"u/666928","width":475},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":218756,"name":"最爱的人","nickname":"人生无常","userid":391165},"follow_times":2,"height":1136,"id":666931,"img":"//i.b0b1.com/250/be/be7418415cf72545a1975195aa5cf0f2067ff0cc.jpg","num":2,"shorturl":"u/666931","width":640},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":221250,"name":"zjkkk","nickname":"taoza","userid":396519},"follow_times":3,"height":556,"id":666933,"img":"//i.b0b1.com/250/cb/cb7aae3ae24060a53d395742b4ac09ffcd1e0ed2.jpg","num":2,"shorturl":"u/666933","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":152157,"name":"rubbish","nickname":"BYYLZ","userid":233500},"follow_times":3,"height":970,"id":666923,"img":"//i.b0b1.com/250/c1/c1f84f1f081d78a615398ba12cf18e3e21f6fc55.jpg","num":2,"shorturl":"u/666923","width":850},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":192753,"name":"1204","nickname":"1214qw","userid":332548},"follow_times":3,"height":1064,"id":666924,"img":"//i.b0b1.com/250/5c/5ceff4b7df293f0a59a28087f7656923109f3002.jpg","num":3,"shorturl":"u/666924","width":710},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":208308,"name":"l12345","nickname":"w105019","userid":367728},"follow_times":1,"height":568,"id":666925,"img":"//i.b0b1.com/250/bd/bdd6025a0aa7fb47ef61c5040d9e3f7621d0db6e.jpg","num":5,"shorturl":"u/666925","width":820}],"pages":100}
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
         * items : [{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":218739,"name":"WANYI","nickname":"oscarzhang","userid":390480},"follow_times":5,"height":1000,"id":666947,"img":"//i.b0b1.com/250/73/732c8df11552ddf287f9ebbf05824275fc17f306.jpg","num":2,"shorturl":"u/666947","width":666},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":192559,"name":"放荡不羁","nickname":"放荡不羁","userid":332086},"follow_times":9,"height":1280,"id":666937,"img":"//i.b0b1.com/250/cb/cb669e2793ba7fc7576b7790dbcb667e47c3f984.jpg","num":2,"shorturl":"u/666937","width":852},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":224475,"name":"欧卡丽娜","nickname":"mocka","userid":349079},"follow_times":9,"height":651,"id":666942,"img":"//i.b0b1.com/250/d6/d66d4b1461202749eb8fc89e1ed605143c2de408.jpg","num":2,"shorturl":"u/666942","width":426},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":226529,"name":"美女图片","nickname":"黑色星期五","userid":407699},"follow_times":7,"height":855,"id":666927,"img":"//i.b0b1.com/250/b0/b0e41e3037cec7d5b501e2becea56f6f75655cde.jpg","num":2,"shorturl":"u/666927","width":570},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":167777,"name":"收口交小奴","nickname":"收口交女奴","userid":264251},"follow_times":2,"height":750,"id":666928,"img":"//i.b0b1.com/250/9a/9a7b5012c8ae9c7084b2bd5229860ee121c5b6c9.jpg","num":2,"shorturl":"u/666928","width":475},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":218756,"name":"最爱的人","nickname":"人生无常","userid":391165},"follow_times":2,"height":1136,"id":666931,"img":"//i.b0b1.com/250/be/be7418415cf72545a1975195aa5cf0f2067ff0cc.jpg","num":2,"shorturl":"u/666931","width":640},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":221250,"name":"zjkkk","nickname":"taoza","userid":396519},"follow_times":3,"height":556,"id":666933,"img":"//i.b0b1.com/250/cb/cb7aae3ae24060a53d395742b4ac09ffcd1e0ed2.jpg","num":2,"shorturl":"u/666933","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":152157,"name":"rubbish","nickname":"BYYLZ","userid":233500},"follow_times":3,"height":970,"id":666923,"img":"//i.b0b1.com/250/c1/c1f84f1f081d78a615398ba12cf18e3e21f6fc55.jpg","num":2,"shorturl":"u/666923","width":850},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":192753,"name":"1204","nickname":"1214qw","userid":332548},"follow_times":3,"height":1064,"id":666924,"img":"//i.b0b1.com/250/5c/5ceff4b7df293f0a59a28087f7656923109f3002.jpg","num":3,"shorturl":"u/666924","width":710},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":208308,"name":"l12345","nickname":"w105019","userid":367728},"follow_times":1,"height":568,"id":666925,"img":"//i.b0b1.com/250/bd/bdd6025a0aa7fb47ef61c5040d9e3f7621d0db6e.jpg","num":5,"shorturl":"u/666925","width":820}]
         * pages : 100
         */

        private int pages;
        private List<ItemsBean> items;

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {
            /**
             * collect : {"avatar":"//static.b0b1.com/images/head.jpg","id":218739,"name":"WANYI","nickname":"oscarzhang","userid":390480}
             * follow_times : 5
             * height : 1000
             * id : 666947
             * img : //i.b0b1.com/250/73/732c8df11552ddf287f9ebbf05824275fc17f306.jpg
             * num : 2
             * shorturl : u/666947
             * width : 666
             */

            private CollectBean collect;
            private int follow_times;
            private int height;
            private int id;
            private String img;
            private int num;
            private String shorturl;
            private int width;

            public CollectBean getCollect() {
                return collect;
            }

            public void setCollect(CollectBean collect) {
                this.collect = collect;
            }

            public int getFollow_times() {
                return follow_times;
            }

            public void setFollow_times(int follow_times) {
                this.follow_times = follow_times;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public String getShorturl() {
                return shorturl;
            }

            public void setShorturl(String shorturl) {
                this.shorturl = shorturl;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public static class CollectBean {
                /**
                 * avatar : //static.b0b1.com/images/head.jpg
                 * id : 218739
                 * name : WANYI
                 * nickname : oscarzhang
                 * userid : 390480
                 */

                private String avatar;
                private int id;
                private String name;
                private String nickname;
                private int userid;

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getNickname() {
                    return nickname;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public int getUserid() {
                    return userid;
                }

                public void setUserid(int userid) {
                    this.userid = userid;
                }
            }
        }
    }
}
