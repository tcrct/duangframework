package com.duangframework.server.netty.handler;

import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.server.utils.RequestUtils;
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
        try {
            RequestUtils.verificationRequest(request);
        }catch (VerificationException ve) {
            //TODO 应该要有信息返回到客户端
            logger.warn(ve.getMessage());
            return;
        }
        // 再开线程执行后续操作，异步操作，提升效率
//        ThreadPoolKit.execute(new ActionHandler(ctx, request));

        //不开线程，因为netty本身就是NIO方式
        new ActionHandler(ctx, request).run();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof VerificationException) {
            logger.warn("Verification Exception: " + cause.getMessage(), cause);
        }
        ctx.fireExceptionCaught(cause);
    }


}
