package com.duangframework.server.netty.kit;

import com.duangframework.core.exceptions.ServerStartUpException;
import com.duangframework.core.interfaces.IContextLoaderListener;
import com.duangframework.core.interfaces.IProcess;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2017/12/12.
 */
public class ServerKit {
    private static Logger logger = LoggerFactory.getLogger(ServerKit.class);
    private static ServerKit _serverKit;
    private static Lock _serverKitLock = new ReentrantLock();
    private String host = "0.0.0.0";
    private int port = 0;
    private IContextLoaderListener contextLoaderList;
    private IProcess mainProcess;

    public static ServerKit duang() {
        if(null == _serverKit) {
            try {
                _serverKitLock.lock();
                _serverKit = new ServerKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _serverKitLock.unlock();
            }
        }
        return _serverKit;
    }

    public ServerKit host(String host) {
        this.host = host;
        return this;
    }

    public ServerKit port(int port) {
        this.port = port;
        return this;
    }

    public ServerKit listener(IContextLoaderListener listener) {
        this.contextLoaderList = listener;
        return this;
    }

    public ServerKit process(IProcess mainProcess) {
        this.mainProcess= mainProcess;
        return this;
    }

    public void start() {
        if(null == contextLoaderList) {
            throw new ServerStartUpException("contextLoaderList is null");
        }
        if(null == mainProcess) {
            throw new ServerStartUpException("mainProcess is null");
        }
        try {
            String serverHost = System.getProperty("server.host");
            if(ToolsKit.isNotEmpty(serverHost)) {
                host = serverHost;
            }
        } catch (Exception e) {}
        try {
            String serverPort = System.getProperty("server.port");
            if(ToolsKit.isNotEmpty(serverPort)) {
                port = Integer.parseInt(serverPort);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        HttpServer httpServer = new HttpServer(host, port, contextLoaderList, mainProcess);
        httpServer.start();
    }
}
