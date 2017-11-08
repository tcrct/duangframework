package com.duangframework.mvc.listener;

/**
 *
 * @author laotang
 * @date 2017/11/5
 */
public class ContextLoaderListener {

    private String context;

    public ContextLoaderListener() {
        contextInitialized();
    }

    /**
     * 容器销毁时监听
     */
    public void contextDestroyed() {
    }

    // 初始化MVC框架，各方法的执行顺序不能变
    public void contextInitialized() {
        initIDuang();
        initPlugins();
        initClasses();
    }

    // 初始化 IDuang
    private void initIDuang() {

    }

    // 初始化插件
    private void initPlugins(){

    }

    // 初始化类，扫描Controller, Service, Entity, Monitor class
    private void initClasses(){

    }

}
