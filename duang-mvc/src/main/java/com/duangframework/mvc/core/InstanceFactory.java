package com.duangframework.mvc.core;

import com.duangframework.core.IHandle;
import com.duangframework.core.interfaces.IPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 *  对象工厂类
 * @author laotang
 * @date 2017/11/8
 */
public class InstanceFactory {

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



}
