package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.common.enums.HttpMethod;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public abstract class AbstractHttpHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractHttpHandler.class);

    private static final String JSON = new AsciiString("application/json; charset=utf-8").toString();

    /**
     * 验证请求是否正确
     * @return
     */
    protected void verificationRequest(FullHttpRequest request) {

        // 保证解析结果正确,否则直接退出
        if (!request.decoderResult().isSuccess()) {
            throw new VerificationException("request decoder is not success, so exit...");
        }

        // 支持的的请求方式
        String method = request.method().toString();
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        if(ToolsKit.isEmpty(httpMethod)) {
            throw new VerificationException("request method["+ httpMethod.toString() +"] is not support, so exit...");
        }

        // uri是有长度的
        String uri = request.uri();
        if (uri == null || uri.trim().length() == 0) {
            throw new VerificationException("request uri length is 0 , so exit...");
        }

        // 如果包含有.则视为静态文件访问
        if(uri.contains(".")) {
            throw new VerificationException("not support static file access, so exit...");
        }
    }

    protected void response(ChannelHandlerContext ctx, boolean keepAlive, String body, Map<String, String> headers) throws Exception {
        // 是否支持Keep-Alive
//        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
        // 构建请求返回对象，并设置返回主体内容结果
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(body, HttpConstants.DEFAULT_CHARSET));
        builderResponseHeader(response, headers);
        HttpHeaderUtil.setKeepAlive(response, keepAlive);
        ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(response);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if(!keepAlive) {
            channelFutureListener.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 设置返回Header头信息
     * @param response
     * @param headers
     */
    private void builderResponseHeader(FullHttpResponse response, Map<String,String> headers) {
        HttpHeaders httpHeaders = response.headers();
        httpHeaders.set(CONTENT_TYPE, JSON); //设置默认的返回结果格式
        httpHeaders.set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        httpHeaders.set(HttpHeaderNames.DATE, ToolsKit.getCurrentDateString());
        if(ToolsKit.isNotEmpty(headers)) {
            for (Iterator<Map.Entry<String, String>> mapIterator = headers.entrySet().iterator(); mapIterator.hasNext(); ) {
                Map.Entry<String, String> entry = mapIterator.next();
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }
        httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()+"");
    }

}
