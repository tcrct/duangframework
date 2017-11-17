package com.duangframework.mvc.core;

import com.duangframework.core.IHandle;
import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.db.Entity;
import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.annotation.ioc.ImportRpc;
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
    public static final Set<Class<? extends Annotation>> MVC_ANNOTATION_SET = new LinkedHashSet<>();
    public static final Set<Class<? extends Annotation>> IOC_ANNOTATION_SET = new LinkedHashSet<>();
    static {
        MVC_ANNOTATION_SET.add(Controller.class);
        MVC_ANNOTATION_SET.add(Service.class);
        MVC_ANNOTATION_SET.add(Monitor.class);
        MVC_ANNOTATION_SET.add(Proxy.class);
        MVC_ANNOTATION_SET.add(Entity.class);

        IOC_ANNOTATION_SET.add(Import.class);
        IOC_ANNOTATION_SET.add(ImportRpc.class);
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
        InstanceFactory.allBeanMaps.put(key ,allBeanMap);
        InstanceFactory.setAllBeanMap(allBeanMap);
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
        InstanceFactory.allBeanMap.putAll(allBeanMap);
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
     * Action Map集合
     * @return
     */
    private static Map<String, Action> actionMapping = new HashMap<>();
    public static Map<String, Action> getActionMapping() { return actionMapping; }
    public static void setActionMapping(Map<String, Action> actionMapping) {
        InstanceFactory.actionMapping.putAll(actionMapping);
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
