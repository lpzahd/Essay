package com.lpzahd.essay.util.foosys;

/**
 * @author lpzahd
 * @describe 傻瓜系统
 * @time 2018/2/28 15:21
 * @change
 */
public abstract class FooSys {


    public static class ClickFooSys extends FooSys {

        public static ClickFooSys newIns() {
            return new ClickFooSys();
        }

        // 自增点击数
        private int autoClickNum;

        /**
         * 自生成回答结果
         * @param hint 暗语[暂且保留]
         */
        public String reduce(String hint) {
            autoClickNum++;

            if(autoClickNum < 20)
                return "点我啊，再怎么点也没反应！";

            if(autoClickNum < 40)
                return "靠，你大爷的，还点！";

            if(autoClickNum < 60)
                return "来啊，互相伤害啊! ^(*￣(oo)￣)^";

            if(autoClickNum < 80)
                return "mmp，点这么多下，单身狗！";

            if(autoClickNum < 100)
                return "本宫不行了！";

            if(autoClickNum < 150)
                return "女婢再也不敢了!";

            if(autoClickNum < 1000)
                return "哼。。。";

            return "......................................";
        }

    }


}
