package com.duangframework.server.netty.server;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author laotang
 * @date 2017/11/8
 */
public class HttpContextListener implements FutureListener<Void> {

    private BootStrap bootStrap;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HttpContextListener(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        if (future.isSuccess()) {
            // 启动上下文监听器
            bootStrap.startContextListener();
            System.out.println("INFO: "+sdf.format(new Date())+" HttpServer["+bootStrap.getHost()+":"+bootStrap.getPort()+"] startup in "+bootStrap.getStartTimeMillis()+" ms, God bless no bugs!");
        } else {
            System.out.println("INFO:  "+sdf.format(new Date())+" HttpServer["+bootStrap.getHost()+":"+bootStrap.getPort()+"] startup failed");
        }
    }
}
