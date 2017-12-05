package com.duangframework.mvc.core;


import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Monitor;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.interfaces.IHandle;
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
    static {
        MVC_ANNOTATION_SET.add(Controller.class);
        MVC_ANNOTATION_SET.add(Service.class);
        MVC_ANNOTATION_SET.add(Monitor.class);
        MVC_ANNOTATION_SET.add(Proxy.class);
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
     * Action Map集合
     * @return
     */
    private static Map<String, Action> actionMapping = new HashMap<>();
    public static Map<String, Action> getActionMapping() { return actionMapping; }
    public static void setActionMapping(Map<String, Action> actionMapping) {
        InstanceFactory.actionMapping.putAll(actionMapping);
    }

    /**
     * Restful Action Map集合
     * @return
     */
    private static Map<String, Action> restfulMapping = new HashMap<>();
    public static Map<String, Action> getRestfulActionMapping() { return restfulMapping; }
    public static void setRestfulActionMapping(Map<String, Action> restfulMapping) {
        InstanceFactory.restfulMapping.putAll(restfulMapping);
    }


}
