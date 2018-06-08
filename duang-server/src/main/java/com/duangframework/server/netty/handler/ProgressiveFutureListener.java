package com.duangframework.server.netty.handler;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

/**
 * @author Created by laotang
 * @date createed in 2018/6/7.
 */
public class ProgressiveFutureListener implements ChannelProgressiveFutureListener {

    private static Logger logger = LoggerFactory.getLogger(ProgressiveFutureListener.class);

    private RandomAccessFile raf;

    public ProgressiveFutureListener(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        if (total < 0) { // total unknown
            logger.debug("{} Transfer progress: {}", future.channel(), progress);
        } else {
            logger.debug("{} Transfer progress: {}/{}", future.channel(), progress, total);
        }
    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        try {
            raf.close();
            logger.debug("{} Transfer complete.", future.channel());
        } catch (Exception e) {
            logger.error("RandomAccessFile close error", e);
        }
    }

    public static ProgressiveFutureListener build(RandomAccessFile raf) {
        return new ProgressiveFutureListener(raf);
    }

}
