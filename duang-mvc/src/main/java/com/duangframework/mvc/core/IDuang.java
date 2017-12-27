package com.duangframework.mvc.core;

/**
 * 定义启动时加载接口方法
 * @author laotang
 * @date 2017/11/5.
 */
public interface IDuang {

    /**
     * 有序添加处理器
     */
    void addHandlers();

    /**
     * 有序添加插件
     */
    void addPlugins();

    /**
     *  初始化其它启动项，在框架启动完成后执行
     */
    void initDuang();

}
