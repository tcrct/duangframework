package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.response.*;
import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpResponse;
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

    protected void response(ChannelHandlerContext ctx, boolean keepAlive, IResponse response) throws Exception {
        // 是否支持Keep-Alive
//        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
        // 构建请求返回对象，并设置返回主体内容结果
        // TODO 这里返回的OK状态码应该根据response这个来确定，待处理
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(response.toString(), HttpConstants.DEFAULT_CHARSET));
        builderResponseHeader(fullHttpResponse, response.getHeaders());
        HttpHeaderUtil.setKeepAlive(fullHttpResponse, keepAlive);
        ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(fullHttpResponse);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if(!keepAlive) {
            channelFutureListener.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // https://www.cnblogs.com/carl10086/p/6185095.html
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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
