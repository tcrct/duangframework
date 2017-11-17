package com.duangframework.core.utils;

import com.duangframework.core.kit.ToolsKit;
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


    public static <T> T newInstance(Class<?> clazz) {
        try {
            logger.debug("\t>>{}", clazz.getCanonicalName());
            return (T)loadClass(clazz.getCanonicalName(), true).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        if (ToolsKit.isEmpty(className)) {
            return null;
        }
        Class<?> cls;
        try {
            if (isInitialized) {
                cls = Class.forName(className, isInitialized, getClassLoader());
            } else {
                cls = getClassLoader().loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Load class is error:" + className, e);
            throw new RuntimeException(e);
        }
        return cls;
    }
}
