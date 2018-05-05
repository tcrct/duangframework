package com.duangframework.server.netty.server;


import com.duangframework.core.exceptions.ServerStartUpException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.server.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.duangframework.rpc.server.RpcServer;

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
//    private RpcServer rpcServer;

    public Server(int port) {
        this("0.0.0.0", port);
    }

    public Server(String host, int port) {
        this.host = host;
        this.httpPort = port;
        this.rpcPort = -1;
    }

    public Server(String host, int port, int rpcPort) {
        this.host = host;
        this.httpPort = port;
        this.rpcPort = rpcPort;
    }

    @Override
    public void start() {

        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpServer = new HttpServer(host, httpPort);
                    httpServer.shutdownHook();
                    httpServer.start();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    throw new ServerStartUpException(e.getMessage());
                }
            }
        });

//        if(rpcPort > -1) {
//        ThreadPoolKit.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    rpcServer = new RpcServer(host, rpcPort);
//                    rpcServer.start();
//                } catch (Exception e) {
//                    logger.warn(e.getMessage(), e);
//                    throw new ServerStartUpException(e.getMessage());
//                }
//            }
//        });
//        }

    }

    @Override
    public void shutdown() {
        if(null != httpServer) {
            httpServer.shutdown();
        }
//        if(null != rpcServer) {
//            rpcServer.shutdown();
//        }
    }



}
