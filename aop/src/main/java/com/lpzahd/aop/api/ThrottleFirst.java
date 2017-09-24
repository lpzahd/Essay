package com.lpzahd.aop.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Author : Lpzahd
 * Date : 二月
 * Desction : (•ิ_•ิ)
 * @link rxjava ThrottleFirst
 */
@Target({METHOD, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface ThrottleFirst {

    long value() default 1000;

}
