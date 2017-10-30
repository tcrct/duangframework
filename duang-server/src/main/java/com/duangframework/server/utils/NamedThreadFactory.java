package com.duangframework.server.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by laotang on 2017/10/30.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger pool_seq = new AtomicInteger(1);
    private final AtomicInteger mThreadNum = new AtomicInteger(1);
    private final String mPrefix;
    private final ThreadGroup mGroup;

    public NamedThreadFactory() {
        this("DuangServer");
    }

    public NamedThreadFactory(String poolName) {
        mPrefix = poolName + "-" + pool_seq.getAndIncrement();
        SecurityManager s = System.getSecurityManager();
        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);
        ret.setDaemon(false);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return mGroup;
    }

}
