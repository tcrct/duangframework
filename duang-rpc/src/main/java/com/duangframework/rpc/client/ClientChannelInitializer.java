package com.duangframework.rpc.client;

import com.duangframework.rpc.common.NettyDecoder;
import com.duangframework.rpc.common.NettyEncoder;
import com.duangframework.rpc.handler.NettyClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(ClientChannelInitializer.class);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        // 将 RPC 请求进行解码（为了处理请求）
        p.addLast(new NettyDecoder());
//        // 将 RPC 响应进行编码（为了返回响应）
        p.addLast(new NettyEncoder());
        //目的是支持异步大文件传输
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new IdleStateHandler(60, 0, 0));
        // 真正处理RPC业务逻辑的地方
        p.addLast(new NettyClientHandler());
    }

    /**
     * Netty抛出异常时触发
     * 抛出异常后，客户端需重新链接服务器端
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
        // 重新链接
        RpcClient.reConnon();
    }
}
