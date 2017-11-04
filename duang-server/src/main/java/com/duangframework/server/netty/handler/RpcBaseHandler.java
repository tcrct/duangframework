package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.RpcRequest;
import com.duangframework.core.exceptions.VerificationException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class RpcBaseHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static Logger logger = LoggerFactory.getLogger(RpcBaseHandler.class);



    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, final RpcRequest request) throws Exception {

        System.out.println("###############: RpcBaseHandler");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof VerificationException) {
            logger.warn("Verification Exception: " + cause.getMessage(), cause);
        }
        ctx.fireExceptionCaught(cause);
    }


}
