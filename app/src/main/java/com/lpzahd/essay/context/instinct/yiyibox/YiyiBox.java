package com.lpzahd.essay.context.instinct.yiyibox;

import java.util.List;

/**
 * 作者 : 迪
 * 时间 : 2017/10/27.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class YiyiBox {

    /**
     * code : 200
     * data : {"items":[{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":214679,"name":"88888888","nickname":"abcd篮颜","userid":378116},"follow_times":36,"height":1200,"id":670305,"img":"//i.b0b1.com/250/76/760289c042acbc3479999f6f609e44844c3d1929.jpg","num":2,"shorturl":"u/670305","width":802},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":10,"height":800,"id":670306,"img":"//i.b0b1.com/250/e3/e388bedcc27639d1687f1a3a29b71f8c14d9624e.jpg","num":2,"shorturl":"u/670306","width":600},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":21,"height":707,"id":670297,"img":"//i.b0b1.com/250/90/9091c23d7685f9caed598e25ce057bef6e44255f.jpg","num":2,"shorturl":"u/670297","width":530},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":1280,"id":670299,"img":"//i.b0b1.com/250/1d/1d39658081af1b39911bf078be37ba27c2493641.jpg","num":4,"shorturl":"u/670299","width":960},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":1205,"id":670300,"img":"//i.b0b1.com/250/d5/d53bab4e46f39d49febd81f2fadb524481a1ccd9.jpg","num":3,"shorturl":"u/670300","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":13,"height":1280,"id":670301,"img":"//i.b0b1.com/250/85/8588b1715c7ab7d77899484fcc89d65a9825604c.jpg","num":2,"shorturl":"u/670301","width":960},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":203102,"name":"企鹅请问","nickname":"wang我符文","userid":355875},"follow_times":27,"height":1200,"id":670302,"img":"//i.b0b1.com/250/69/69f0252baf5f7959cd665d8480cc07a95b2b383f.jpg","num":2,"shorturl":"u/670302","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":799,"id":670303,"img":"//i.b0b1.com/250/40/401a597e4a35a4430ceb723b3262c0587e291225.jpg","num":6,"shorturl":"u/670303","width":1200},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":88538,"name":"dp","nickname":"dapengzhanchi","userid":103711},"follow_times":89,"height":2272,"id":670304,"img":"//i.b0b1.com/250/f9/f9843567ae3cab44cc40650ccc192dee8e7ef32c.jpg","num":2,"shorturl":"u/670304","width":1704},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":224933,"name":"黑丝御姐","nickname":"Weeeee","userid":403584},"follow_times":18,"height":1024,"id":670290,"img":"//i.b0b1.com/250/c4/c4aa7fe5b85c2deac5148123289bcf0a2e60cca4.jpg","num":2,"shorturl":"u/670290","width":768}],"pages":100}
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
         * items : [{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":214679,"name":"88888888","nickname":"abcd篮颜","userid":378116},"follow_times":36,"height":1200,"id":670305,"img":"//i.b0b1.com/250/76/760289c042acbc3479999f6f609e44844c3d1929.jpg","num":2,"shorturl":"u/670305","width":802},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":10,"height":800,"id":670306,"img":"//i.b0b1.com/250/e3/e388bedcc27639d1687f1a3a29b71f8c14d9624e.jpg","num":2,"shorturl":"u/670306","width":600},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":21,"height":707,"id":670297,"img":"//i.b0b1.com/250/90/9091c23d7685f9caed598e25ce057bef6e44255f.jpg","num":2,"shorturl":"u/670297","width":530},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":1280,"id":670299,"img":"//i.b0b1.com/250/1d/1d39658081af1b39911bf078be37ba27c2493641.jpg","num":4,"shorturl":"u/670299","width":960},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":1205,"id":670300,"img":"//i.b0b1.com/250/d5/d53bab4e46f39d49febd81f2fadb524481a1ccd9.jpg","num":3,"shorturl":"u/670300","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":13,"height":1280,"id":670301,"img":"//i.b0b1.com/250/85/8588b1715c7ab7d77899484fcc89d65a9825604c.jpg","num":2,"shorturl":"u/670301","width":960},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":203102,"name":"企鹅请问","nickname":"wang我符文","userid":355875},"follow_times":27,"height":1200,"id":670302,"img":"//i.b0b1.com/250/69/69f0252baf5f7959cd665d8480cc07a95b2b383f.jpg","num":2,"shorturl":"u/670302","width":800},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":27209,"name":"tutututu","nickname":"AKKad","userid":32054},"follow_times":7,"height":799,"id":670303,"img":"//i.b0b1.com/250/40/401a597e4a35a4430ceb723b3262c0587e291225.jpg","num":6,"shorturl":"u/670303","width":1200},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":88538,"name":"dp","nickname":"dapengzhanchi","userid":103711},"follow_times":89,"height":2272,"id":670304,"img":"//i.b0b1.com/250/f9/f9843567ae3cab44cc40650ccc192dee8e7ef32c.jpg","num":2,"shorturl":"u/670304","width":1704},{"collect":{"avatar":"//static.b0b1.com/images/head.jpg","id":224933,"name":"黑丝御姐","nickname":"Weeeee","userid":403584},"follow_times":18,"height":1024,"id":670290,"img":"//i.b0b1.com/250/c4/c4aa7fe5b85c2deac5148123289bcf0a2e60cca4.jpg","num":2,"shorturl":"u/670290","width":768}]
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
             * collect : {"avatar":"//static.b0b1.com/images/head.jpg","id":214679,"name":"88888888","nickname":"abcd篮颜","userid":378116}
             * follow_times : 36
             * height : 1200
             * id : 670305
             * img : //i.b0b1.com/250/76/760289c042acbc3479999f6f609e44844c3d1929.jpg
             * num : 2
             * shorturl : u/670305
             * width : 802
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
                 * id : 214679
                 * name : 88888888
                 * nickname : abcd篮颜
                 * userid : 378116
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
