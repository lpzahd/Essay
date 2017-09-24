package com.lpzahd.aop.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ) 消息日志
 */

@Target({TYPE, METHOD, CONSTRUCTOR})
@Retention(CLASS)
public @interface Log {
}
