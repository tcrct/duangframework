package com.duangframework.core.annotation.aop;

import java.lang.annotation.*;

/**
 *  代理类注解
 * @author Created by laotang
 * @date on 2017/11/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
    Class<? extends Annotation> aop();
    boolean init() default  false;		//是否需要在框架启动完成后，进行初始化操作
}