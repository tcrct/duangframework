package com.duangframework.server.netty.server;

import com.duangframework.mvc.listener.ContextLoaderListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/11/8
 */
public class DuangContextListener implements FutureListener<Void> {

    private static Logger logger = LoggerFactory.getLogger(DuangContextListener.class);
    private BootStrap bootStrap;

    public DuangContextListener(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        if (future.isSuccess()) {
            startDuangContextListener();
            logger.warn("netty server started on endpoint : " + bootStrap.getSockerAddress().toString());
        } else {
            logger.warn("netty server started failed");
        }
    }

    private void startDuangContextListener() {
        new ContextLoaderListener();
    }
}
