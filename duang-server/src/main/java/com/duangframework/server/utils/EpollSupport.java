package com.duangframework.server.utils;
public final class EpollSupport {

    private static final boolean SUPPORT_EPOLL_ET;

    static {
        boolean epoll;
        try {
            Object isAvailable = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
            epoll = (null != isAvailable) && Boolean.valueOf(isAvailable.toString());
        } catch (Throwable e) {
            try {
                Class.forName("io.netty.channel.epoll.Native");
                epoll = true;
            } catch (Throwable throwable) {
                epoll = false;
            }
        }
        SUPPORT_EPOLL_ET = epoll;
    }

    /**
     * The native socket transport for Linux using JNI.
     */
    public static boolean isSupportEpoll() {
        return SUPPORT_EPOLL_ET;
    }
}
