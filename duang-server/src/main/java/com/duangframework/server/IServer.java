package com.duangframework.server;

/**
 *
 * @author  Created by laotang on 2017/10/30.
 */
public interface IServer {

   /**
     *  启动
     */
    void start();

    /**
     * 停止
     * @return
     */
    void shutdown();
}
