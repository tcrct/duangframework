package com.duangframework.core.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityCache {
    /**
     * 自定义缓存Key，支持表达式
     * @return String 自定义缓存Key
     */
    String key() default "";
    /**
     * 缓存的过期时间，单位：秒，如果为0则表示永久缓存, 默认为600秒
     * @return 时间
     */
    int ttl() default 600;
}