package com.duangframework.mvc;

import com.duangframework.mvc.core.IDuang;

/**
 * 核心类，配置MVC框架启动是的各项处理器，插件，拦截器等
 * 在容器启动时加载
 * @author  laotang
 * @date 2017/11/5.
 */
public class Duang implements IDuang {


    /**
     * 添加自定义处理器到框架，添加后，框架会对客户端的请求按顺序执行，发生异常时中断执行，并返回异常信息
     */
    @Override
    public void addHandlers() {
        System.out.println("addHandlers");
    }

    /**
     * 添加自定义插件到框架
     */
    @Override
    public void addPlugins() {
        System.out.println("addPlugins");
    }

    @Override
    public void initDuang() {

    }
}
