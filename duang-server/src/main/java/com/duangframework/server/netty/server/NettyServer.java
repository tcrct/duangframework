package com.duangframework.server.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by laotang on 2017/10/30.
 */
public class NettyServer extends AbstractNettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public EventLoopGroup bossGroup;
    public EventLoopGroup workerGroup;
    public ServerBootstrap serverBootstrap;
    protected volatile ByteBufAllocator allocator;
    private String host;
    private int port;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        init();//初始化
    }

    private void init() {
        serverBootstrap = new ServerBootstrap();
        bossGroup = NettyServerFactory.builderBossLoopGroup();
        workerGroup = NettyServerFactory.builderWorkerLoopGroup();

        serverBootstrap.group(bossGroup, workerGroup);
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        serverBootstrap.option(ChannelOption.SO_BACKLOG, NettyServerConfig.SO_BACKLOG)  //连接数
                .childOption(ChannelOption.ALLOCATOR, allocator)
                .childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)  //开启Keep-Alive，长连接
                .childOption(ChannelOption.TCP_NODELAY, true)  //不延迟，消息立即发送
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
    }

    @Override
    public void start() {
        if (isNative()) {
            serverBootstrap.channel(EpollServerSocketChannel.class);
        } else {
            serverBootstrap.channel(NioServerSocketChannel.class);
        }
        InetSocketAddress httpSockerAddress = new InetSocketAddress(host, port);
        serverBootstrap.localAddress(httpSockerAddress)
                .handler(new LoggingHandler(LogLevel.WARN))
                .childHandler(new HttpChannelInitializer());

//        InetSocketAddress rpcSockerAddress = new InetSocketAddress(host, port+10000);
//        serverBootstrap.localAddress(rpcSockerAddress).childHandler(new HttpChannelInitializer());

        try {
            ChannelFuture future = serverBootstrap.bind().sync();
            future.addListener(new FutureListener<Void>() {
                @Override
                public void operationComplete(Future<Void> future) throws Exception {
                    if (future.isSuccess()) {
                        logger.warn("netty server started on endpoint : " + host+":"+port);
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
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    @Override
    public void shutdown() {
        try {
            if(null != workerGroup) {
                workerGroup.shutdownGracefully();
            }
            if(null != bossGroup) {
                bossGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            logger.warn("Netty Server shutdown exception: "+e.getMessage(), e);
        }
    }
}
