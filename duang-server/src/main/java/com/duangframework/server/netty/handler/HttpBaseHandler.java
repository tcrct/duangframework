package com.duangframework.server.netty.handler;

import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.core.kit.ThreadPoolKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class HttpBaseHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(HttpBaseHandler.class);



    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {

        // 再开线程执行后续操作，异步操作，提升效率
        ThreadPoolKit.execute(new ActionHandler(ctx, request));

//        new NettyMainFilter(ctx, request, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof VerificationException) {
            logger.warn("Verification Exception: " + cause.getMessage(), cause);
        }
        ctx.fireExceptionCaught(cause);
    }


}
