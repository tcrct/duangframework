package com.duangframework.server.netty.handler;

import com.duangframework.mvc.NettyMainFilter;
import com.duangframework.server.common.enums.HttpMethod;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class HttpServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(HttpServiceHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        // 保证解析结果正确,否则直接退出
        if (!request.decoderResult().isSuccess()) {
            logger.warn("request decoder is not success, so exit...");
            return;
        }

        // 支持的的请求方式
        HttpMethod httpMethod = HttpMethod.valueOf(request.method().toString());
        if(null == httpMethod) {
            logger.warn("request method["+ httpMethod.toString() +"] is not support, so exit...");
            return ;
        }

        // uri是有长度的
        final String uri = request.uri();
        if (uri == null || uri.trim().length() == 0) {
            logger.warn("request uri length is 0 , so exit...");
            return;
        }

        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);

        // 设置返回结果
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE,new AsciiString("application/json; charset=utf-8")); //设置默认的返回结果
        response.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        HttpHeaderUtil.setKeepAlive(response, keepAlive);
        new NettyMainFilter(ctx, request, response);
    }
}
