package com.duangframework.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/24.
 */
public class BeanUtils {

    /**
     * 所有Class Bean集合， 这里的所有是指@DefaultClassTemplate扫描后且实例化后的
     * 经过滤后的Class并Class.forName()
     * key为MVC_ANNOTATION_SET的每一个元素
     * value为对应的Bean
     * 当key为Controller时，具体内容为： Map<"BaseController", Map<ControllerName, ControllerBean>>
     */
    private static Map<String,Map<String,Object>> allBeanMaps = new HashMap<>();
    public static Map<String,Map<String,Object>> getAllBeanMaps() {
        return allBeanMaps;
    }
    public static void setAllBeanMaps(String key, Map<String, Object> allBeanMap) {
        BeanUtils.allBeanMaps.put(key ,allBeanMap);
        BeanUtils.setAllBeanMap(allBeanMap);
    }

    /**
     * 所有Class Bean集合， 这里的所有是指@DefaultClassTemplate扫描后且实例化后的
     * 经过滤后的Class并Class.forName()
     * key为 className
     * value为class的Bean
     */
    private static Map<String,Object> allBeanMap = new HashMap<>();
    public static Map<String,Object> getAllBeanMap() {
        return allBeanMap;
    }
    public static void setAllBeanMap(Map<String, Object> allBeanMap) {
        BeanUtils.allBeanMap.putAll(allBeanMap);
    }
}
