package com.duangframework.server.netty.server;

import com.duangframework.server.IServer;
import com.duangframework.server.utils.NativeSupport;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.Executor;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public abstract class AbstractNettyServer implements IServer {

    /**
     * 创建线程池组
     * @param workers       工作线程数
     * @param executor      线程池
     * @return
     */
    public EventLoopGroup initEventLoopGroup(int workers, Executor executor) {
        return isNative() ? new EpollEventLoopGroup(workers, executor) : new NioEventLoopGroup(workers, executor);
    }

    public boolean isNative() {
        return NativeSupport.isSupportNative();
    }
}
