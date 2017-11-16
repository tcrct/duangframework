package com.duangframework.mvc.core;

import com.duangframework.core.IHandle;
import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.db.Entity;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Monitor;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.interfaces.IPlugin;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 *  对象工厂类
 * @author laotang
 * @date 2017/11/8
 */
public class InstanceFactory {

    /**
     * 注解类
     */
    public static final Set<Class<? extends Annotation>> ANNOTATION_SET = new LinkedHashSet<>();
    static {
        ANNOTATION_SET.add(Controller.class);
        ANNOTATION_SET.add(Service.class);
        ANNOTATION_SET.add(Monitor.class);
        ANNOTATION_SET.add(Proxy.class);
        ANNOTATION_SET.add(Entity.class);
    }



    /**
     * 处理器集合
     */
    private static List<IHandle> handles = new ArrayList<>();
    public static List<IHandle> getHandles() {
        return InstanceFactory.handles;
    }
    public static void setHandles(IHandle handle) {
        InstanceFactory.handles.add(handle);
    }

    /**
     * 插件集合
     */
    private static List<IPlugin> plugins = new ArrayList<>();
    public static List<IPlugin> getPlugins() {
        return plugins;
    }
    public static void setPlugin(IPlugin plugin) {
        InstanceFactory.plugins.add(plugin);
    }

    /**
     * 所有Class集合， 这里的所有是指@DefaultClassTemplate扫描后的
     * 即经过滤后的Class
     */
    private static Map<String, List<Class<?>>> allClassMap = new HashMap<>();
    public static Map<String, List<Class<?>>> getAllClassMap() {
        return allClassMap;
    }
    public static void setAllClassMap(Map<String, List<Class<?>>> allClassMap) {
        InstanceFactory.allClassMap.putAll(allClassMap);
    }

    /**
     * 代理类集合
     * key为Service方法添加的注解类名称
     * value为代理类实例对象
     */
    private static Map<String, Class<?>> proxyMap = new HashMap<>();
    public static Map<String, Class<?>> getProxyMap() {
        return proxyMap;
    }
    public static void setProxyMap(Map<String, Class<?>> proxyMap) {
        InstanceFactory.proxyMap.putAll(proxyMap);
    }






    /**
     *  所有Controller Class集合
     *  key 为Class的全名，包括包路径
     */
    private static Map<String, Class<?>> controllerClassMap= new HashMap<>();
    public static Map<String, Class<?>> getControllerClassMap() {
        return controllerClassMap;
    }
    public static void addControllerClass2Map(Class<?> controllerClass) {
        InstanceFactory.controllerClassMap.put(controllerClass.getCanonicalName().trim(), controllerClass);
    }

    /**
     * 所有Service Class集合
     * key 为Class的全名，包括包路径
     */
    private static Map<String, Class<?>> serviceClassMap = new HashMap<>();
    public static Map<String, Class<?>> getServiceClassMap() {
        return serviceClassMap;
    }
    public static void addServiceClass2Map(Class<?> serviceClass) {
        InstanceFactory.serviceClassMap.put(serviceClass.getCanonicalName().trim(), serviceClass);
    }

}
