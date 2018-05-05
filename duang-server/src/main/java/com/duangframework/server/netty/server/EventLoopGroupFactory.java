package com.duangframework.server.netty.server;

import com.duangframework.server.utils.NamedThreadFactory;
import com.duangframework.server.utils.EpollSupport;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.*;

/**
 * Created by laotang on 2017/10/30.
 */
public class EventLoopGroupFactory {

    public static EventLoopGroup builderBossLoopGroup() {

        Executor executor = builderThreadPoolExecutor(
                ServerConfig.MAX_BOSS_EXECUTORS_NUMBER,
                ServerConfig.MAX_BOSS_EXECUTORS_NUMBER,
                ServerConfig.KEEP_ALIVETIME,
                TimeUnit.HOURS,
                new ArrayBlockingQueue<Runnable>(ServerConfig.MAX_BOSS_EXECUTORS_NUMBER),
                new NamedThreadFactory(ServerConfig.BOSSGROUP_POOLTHREAD_NAME));


        if(EpollSupport.isSupportEpoll()) {
            EpollEventLoopGroup bossLoopGroup = new EpollEventLoopGroup(ServerConfig.MAX_BOSS_EXECUTORS_NUMBER, executor);
            bossLoopGroup.setIoRatio(ServerConfig.IO_RATIO_NUMBER);
            return bossLoopGroup;
        } else {
            NioEventLoopGroup bossLoopGroup = new NioEventLoopGroup(ServerConfig.MAX_BOSS_EXECUTORS_NUMBER, executor);
            bossLoopGroup.setIoRatio(ServerConfig.IO_RATIO_NUMBER);
            return bossLoopGroup;
        }
    }

    public static EventLoopGroup builderWorkerLoopGroup() {

        int workerNum = Runtime.getRuntime().availableProcessors() << 1;

        Executor executor = builderThreadPoolExecutor(
                workerNum,
                workerNum,
                ServerConfig.KEEP_ALIVETIME,
                TimeUnit.HOURS,
                new ArrayBlockingQueue<Runnable>(workerNum),
                new NamedThreadFactory(ServerConfig.WORKERGROUP_POOLTHREAD_NAME));

        if(EpollSupport.isSupportEpoll()) {
            EpollEventLoopGroup workerLoopGroup = new EpollEventLoopGroup(workerNum, executor);
            workerLoopGroup.setIoRatio(ServerConfig.IO_RATIO_NUMBER);
            return workerLoopGroup;
        } else {
            NioEventLoopGroup workerLoopGroup = new NioEventLoopGroup(workerNum, executor);
            workerLoopGroup.setIoRatio(ServerConfig.IO_RATIO_NUMBER);
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
