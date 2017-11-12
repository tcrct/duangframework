package com.duangframework.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by laotang on 2017/11/12 0012.
 */
public class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
