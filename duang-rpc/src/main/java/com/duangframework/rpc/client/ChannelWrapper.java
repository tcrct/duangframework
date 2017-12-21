package com.duangframework.rpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Created by laotang on 2017/2/8.
 */
public class ChannelWrapper {

    private final ChannelFuture channelFuture;

    public ChannelWrapper(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public boolean isOK() {
        return (this.channelFuture.channel() != null && this.channelFuture.channel().isActive());
    }

    public boolean isWriteable() {
        return this.channelFuture.channel().isWritable();
    }

    public Channel getChannel() {
        return this.channelFuture.channel();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

}
