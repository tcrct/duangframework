package com.duangframework.core.utils;

import com.duangframework.core.exceptions.MvcStartUpException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/24.
 */
public class BeanUtils {

    /**
     *  根据Class取出对应的Bean
     */
    public static Object getBean(Class<?> clazz, Object targetObj) {
        if (!allBeanMap.containsKey(clazz)) {
            if(!targetObj.getClass().equals(Class.class)) {
                throw new MvcStartUpException(targetObj.getClass().getName() + " 无法根据类名获取实例: " + clazz + " , 请检查是否后缀名是否正确！");
            }
        }
        return allBeanMap.get(clazz);
    }

    /**
     * 所有Class Bean集合， 这里的所有是指@DefaultClassTemplate扫描后且实例化后的
     * 经过滤后的Class并Class.forName()
     * key为 Class
     * value为Class的Bean
     */
    private static Map<Class<?>, Object> allBeanMap = new HashMap<>();
    public static Map<Class<?>, Object> getAllBeanMap() {
        return allBeanMap;
    }
    public static void setAllBeanMap(Map<Class<?>, Object> beanMap) {
        allBeanMap.putAll(beanMap);
    }
    public static void setBean2Map(Class<?> cls, Object bean) {
        BeanUtils.allBeanMap.put(cls, bean);
    }


    /**
     * 所有Class Bean集合， 这里的所有是指@DefaultClassTemplate扫描后且实例化后的
     * 经过滤后的Class并Class.forName()
     * key为MVC_ANNOTATION_SET的每一个元素
     * value为对应的Bean
     * 当key为Controller时，具体内容为： Map<"Controller", Map<ControllerClass ControllerBean>>
     */
    private static Map<String, Map<Class<?>, Object>> allBeanMaps = new HashMap<>();
    public static Map<String,Map<Class<?>, Object>> getAllBeanMaps() {
        return allBeanMaps;
    }
    public static void setAllBeanMaps(String key, Map<Class<?>, Object> allBeanMap) {
        allBeanMaps.put(key, allBeanMap);
        setAllBeanMap(allBeanMap);
    }


}
