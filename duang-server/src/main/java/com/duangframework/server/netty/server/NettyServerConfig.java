package com.duangframework.server.netty.server;

/**
 * Created by laotang on 2017/10/30.
 */
public class NettyServerConfig {

    //boss线程数，建议为1
    public static int MAX_BOSS_EXECUTORS_NUMBER = 1;
    public static final String BOSSGROUP_POOLTHREAD_NAME = "Netty.BossGroup";
    public static final String WORKERGROUP_POOLTHREAD_NAME = "Netty.WrokerGroup";

    public static long KEEP_ALIVETIME = 2L;

    public static int IO_RATIO_NUMBER = 100;

    public static int SO_BACKLOG = 32768;

}
