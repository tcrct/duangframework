package com.duangframework.mvc;


import com.duangframework.core.exceptions.ServerStartUpException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.mvc.server.HttpServer;
import com.duangframework.rpc.server.RpcServer;
import com.duangframework.server.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by laotang
 * @date on 2017/12/8.
 */
public class Server implements IServer {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private String host;
    private int httpPort;
    private int rpcPort;
    private HttpServer httpServer;
    private RpcServer rpcServer;

    public Server(int port) {
        this("0.0.0.0", port);
    }

    public Server(String host, int port) {
        this.host = host;
        this.httpPort = port;
        // http端口 + 1 为rpc请求端口
        this.rpcPort = port + 1;
    }

    @Override
    public void start() {

        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpServer = new HttpServer(host, httpPort);
                    httpServer.start();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    throw new ServerStartUpException(e.getMessage());
                }
            }
        });

        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rpcServer = new RpcServer(host, rpcPort);
                    rpcServer.start();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    throw new ServerStartUpException(e.getMessage());
                }
            }
        });

    }

    @Override
    public void shutdown() {
        if(null != httpServer) {
            httpServer.shutdown();
        }
        if(null != rpcServer) {
            rpcServer.shutdown();
        }
    }

}
