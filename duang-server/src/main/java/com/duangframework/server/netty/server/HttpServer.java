package com.duangframework.server.netty.server;

import com.duangframework.core.interfaces.IContextLoaderListener;
import com.duangframework.core.interfaces.IProcess;
import io.netty.channel.ChannelFuture;

/**
 * @author Created by laotang
 * @date on 2017/12/8.
 */
public class HttpServer extends AbstractNettyServer {

    public HttpServer(int port) {
        this("0.0.0.0", port);
    }

    public HttpServer(String host, int port) {
        super(host, port);
    }

    public HttpServer(String host, int port, boolean isDebug, IContextLoaderListener listenerCls, IProcess mainProcessCls) {
        super(host, port, isDebug, listenerCls, mainProcessCls);
    }

    @Override
    public void start() {
        nettyBootstrap.localAddress(bootStrap.getSockerAddress())
                .handler(bootStrap.getLoggingHandler())
                .childHandler(new HttpChannelInitializer(bootStrap));
        try {
            ChannelFuture future = nettyBootstrap.bind().sync();
            future.addListener(new HttpContextListener(bootStrap));
            writePidFile();
            // 等待或监听数据全部完成
            future.channel().closeFuture().awaitUninterruptibly();
            //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
            future.channel().closeFuture().sync();//相当于在这里阻塞，直到Server Channel关闭
        } catch (InterruptedException e) {
            throw new RuntimeException("this.nettyBootstrap.bind().sync() InterruptedException", e);
        } finally {
            clearPidFile();
            shutdown();
        }
    }
}
