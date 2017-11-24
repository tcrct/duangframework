package com.duangframework.core.interfaces;

/**
 * 定义插件类的接口
 * @author laotang
 * @date 2017/11/8
 */
public interface IPlugin {

    /**
     * 插件初始化
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 插件启动
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 插件停止
     * @throws Exception
     */
    void stop() throws Exception;

}
