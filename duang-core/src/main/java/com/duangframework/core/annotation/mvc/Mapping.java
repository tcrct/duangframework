package com.duangframework.core.annotation.mvc;

/**
 * Created by laotang on 2017/11/5.
 */

import java.lang.annotation.*;

/**
 * 定义 Mapping 类注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Mapping {
    String value() default "";
    String desc() default "";
    int level () default 0;
    int order() default 0;
    long timeout() default 3000L;
}
