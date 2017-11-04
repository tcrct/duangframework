package com.duangframework.server.netty.server;

import com.duangframework.server.IServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by laotang on 2017/10/30.
 */
public class Server implements IServer {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public EventLoopGroup bossGroup;
    public EventLoopGroup workerGroup;
    public ServerBootstrap serverBootstrap;
    protected volatile ByteBufAllocator allocator;
    private BootStrap bootStrap;

    public Server(int port) {
       this("0.0.0.0", port);
    }

    public Server(String host, int port) {
        bootStrap = new BootStrap(host, port);
        init();//初始化
    }

    private void init() {
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bootStrap.getBossGroup(), bootStrap.getWorkerGroup());
        serverBootstrap.option(ChannelOption.SO_BACKLOG, bootStrap.getBockLog())  //连接数
                .childOption(ChannelOption.ALLOCATOR, bootStrap.getAllocator())
                .childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)  //开启Keep-Alive，长连接
                .childOption(ChannelOption.TCP_NODELAY, true)  //不延迟，消息立即发送
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
        serverBootstrap.channel(bootStrap.getDefaultChannel());
    }

    @Override
    public void start() {
        serverBootstrap.localAddress(bootStrap.getSockerAddress())
                .handler(bootStrap.getLoggingHandler())
                .childHandler(new DuangChannelInitializer(bootStrap));
//        InetSocketAddress rpcSockerAddress = new InetSocketAddress(host, port+10000);
//        serverBootstrap.localAddress(rpcSockerAddress).childHandler(new DuangChannelInitializer());

        try {
            ChannelFuture future = serverBootstrap.bind().sync();
            future.addListener(new FutureListener<Void>() {
                @Override
                public void operationComplete(Future<Void> future) throws Exception {
                    if (future.isSuccess()) {
                        logger.warn("netty server started on endpoint : " + bootStrap.getSockerAddress().toString());
                    } else {
                        logger.warn("netty server started failed");
                    }
                }
            });
            // 等待或监听数据全部完成
            future.channel().closeFuture().awaitUninterruptibly();
            //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
            future.channel().closeFuture().sync();//相当于在这里阻塞，直到serverchannel关闭
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        } finally {
            shutdown();
        }

    }

    @Override
    public void shutdown() {
        try {
            bootStrap.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
