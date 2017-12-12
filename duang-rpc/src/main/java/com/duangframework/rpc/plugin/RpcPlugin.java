package com.duangframework.rpc.plugin;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.rpc.server.RpcServer;

/**
 * @author Created by laotang
 * @date on 2017/12/6.
 */
public class RpcPlugin implements IPlugin {

    private static RpcServer rpcServer;

    @Override
    public void init() throws Exception {
        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                String host = ConfigKit.duang().key("rpc.host").defaultValue("0.0.0.0").asString();
                int port = ConfigKit.duang().key("rpc.port").defaultValue(9091).asInt();
                rpcServer = new RpcServer(host, port);
                try {
                    rpcServer.start();
                } catch (Exception e) {
                    rpcServer.shutdown();
                }
            }
        });

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        rpcServer.shutdown();
    }
}
