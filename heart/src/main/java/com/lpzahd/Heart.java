package com.lpzahd;

/**
 * 凡心所向，素履所往，生如逆旅，一苇以航
 */
public interface Heart<E> {

    E heart();

    /**
     * 将心比心
     */
    E heartToHeart(Heart heart);

    /**
     * 自然吸引
     */
    E charmToHeart(Charm charm);

    /**
     * 扣入心扉
     */
    E effortToHeart(Effort effort);

    /**
     * 有钱能使鬼推磨
     */
    E valueToHeart(Value value);

    /**
     * 魅力值
     */
    Charm charm();

    /**
     * 努力值
     */
    Effort effort();

    /**
     * 价值
     */
    Value value();

}
