package com.duangframework.server.netty.server;

import com.duangframework.server.netty.handler.HttpServiceHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        // HttpServerCodec包含了默认的HttpRequestDecoder和HttpResponseEncoder
        p.addLast(new HttpServerCodec());
        // 为http响应内容添加gizp压缩器
        p.addLast(new HttpContentCompressor());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new ChunkedWriteHandler());
        // 真正处理用户业务逻辑的地方
        p.addLast(new HttpServiceHandler());
    }
}
