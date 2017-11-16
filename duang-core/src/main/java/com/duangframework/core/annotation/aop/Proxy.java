package com.duangframework.core.annotation.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  代理类注解
 * @author Created by laotang
 * @date on 2017/11/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
    Class<?> aop();
    boolean init() default  false;		//是否需要在框架启动完成后，进行初始化操作
}