package com.duangframework.server.netty.server;

import com.duangframework.server.utils.NamedThreadFactory;
import com.duangframework.server.utils.NativeSupport;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.*;

/**
 * Created by laotang on 2017/10/30.
 */
public class NettyServerFactory {

    public static EventLoopGroup builderBossLoopGroup() {

        Executor executor = builderThreadPoolExecutor(
                NettyServerConfig.MAX_BOSS_EXECUTORS_NUMBER,
                NettyServerConfig.MAX_BOSS_EXECUTORS_NUMBER,
                NettyServerConfig.KEEP_ALIVETIME,
                TimeUnit.HOURS,
                new ArrayBlockingQueue<Runnable>(NettyServerConfig.MAX_BOSS_EXECUTORS_NUMBER),
                new NamedThreadFactory(NettyServerConfig.BOSSGROUP_POOLTHREAD_NAME));


        if(NativeSupport.isSupportNative()) {
            EpollEventLoopGroup bossLoopGroup = new EpollEventLoopGroup(NettyServerConfig.MAX_BOSS_EXECUTORS_NUMBER, executor);
            bossLoopGroup.setIoRatio(NettyServerConfig.IO_RATIO_NUMBER);
            return bossLoopGroup;
        } else {
            NioEventLoopGroup bossLoopGroup = new NioEventLoopGroup(NettyServerConfig.MAX_BOSS_EXECUTORS_NUMBER, executor);
            bossLoopGroup.setIoRatio(NettyServerConfig.IO_RATIO_NUMBER);
            return bossLoopGroup;
        }
    }

    public static EventLoopGroup builderWorkerLoopGroup() {

        int workerNum = Runtime.getRuntime().availableProcessors() << 1;

        Executor executor = builderThreadPoolExecutor(
                workerNum,
                workerNum,
                NettyServerConfig.KEEP_ALIVETIME,
                TimeUnit.HOURS,
                new ArrayBlockingQueue<Runnable>(workerNum),
                new NamedThreadFactory(NettyServerConfig.WORKERGROUP_POOLTHREAD_NAME));

        if(NativeSupport.isSupportNative()) {
            EpollEventLoopGroup workerLoopGroup = new EpollEventLoopGroup(workerNum, executor);
            workerLoopGroup.setIoRatio(NettyServerConfig.IO_RATIO_NUMBER);
            return workerLoopGroup;
        } else {
            NioEventLoopGroup workerLoopGroup = new NioEventLoopGroup(workerNum, executor);
            workerLoopGroup.setIoRatio(NettyServerConfig.IO_RATIO_NUMBER);
            return workerLoopGroup;
        }
    }

    private static Executor builderThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory);
    }

}
