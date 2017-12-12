package com.duangframework.server.netty.server;

import com.duangframework.server.netty.handler.HttpBaseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(HttpChannelInitializer.class);

    private BootStrap bootStrap;
    private SslContext sslContext;

    public HttpChannelInitializer(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if(bootStrap.isSslEnabled()) {
            sslContext = bootStrap.getSslContext();
        }
        // HttpServerCodec包含了默认的HttpRequestDecoder(请求消息解码器)和HttpResponseEncoder(响应解码器)
        p.addLast(new HttpServerCodec());
        // 为http响应内容添加gizp压缩器
        p.addLast(new HttpContentCompressor());
        //目的是将多个消息转换为单一的request或者response对象
        p.addLast(new HttpObjectAggregator(1048576));
        //目的是支持异步大文件传输
        p.addLast(new ChunkedWriteHandler());
        // 真正处理HTTP业务逻辑的地方
        p.addLast(new HttpBaseHandler(bootStrap));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
    }
}
