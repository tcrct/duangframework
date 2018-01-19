package com.duangframework.server.utils;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.http.response.IResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by laotang on 2017/11/8.
 */
public class ResponseUtils {

    /**
     * 构建Response对象
     * @param request
     * @return
     */
    public static IResponse buildDuangResponse(IRequest request) {
        Map<String, String > headers = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }
        return new HttpResponse(headers, request.getCharacterEncoding(), request.getContentType());
    }

    public static void buildFullHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(message, HttpConstants.DEFAULT_CHARSET));
        response.headers().setAll(request.headers());
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        HttpHeaders.setKeepAlive(response, keepAlive);
        ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(response);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if(!keepAlive) {
            channelFutureListener.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
