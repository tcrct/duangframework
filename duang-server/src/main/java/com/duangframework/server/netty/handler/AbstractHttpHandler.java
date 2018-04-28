package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public abstract class AbstractHttpHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractHttpHandler.class);

    private static final String JSON = new AsciiString("application/json;charset=utf-8").toString();
    private static final String TEXT = new AsciiString("text/html;charset=UTF-8").toString();

    protected void response(ChannelHandlerContext ctx, boolean keepAlive, IRequest reuqest, IResponse response) throws Exception {
        // 构建请求返回对象，并设置返回主体内容结果
        HttpResponseStatus status = response.getStatus() == 200 ? HttpResponseStatus.OK : HttpResponseStatus.INTERNAL_SERVER_ERROR;
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(response.toString(), HttpConstants.DEFAULT_CHARSET));
        builderResponseHeader(fullHttpResponse, reuqest, response);
        HttpHeaders.setKeepAlive(fullHttpResponse, keepAlive);
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
     * @param request
     */
    private void builderResponseHeader(FullHttpResponse fullHttpResponse, IRequest request, IResponse response) {
        HttpHeaders responseHeaders = fullHttpResponse.headers();
        String conentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        if(ToolsKit.isEmpty(conentType)) {
            conentType = JSON;
        }
        responseHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), conentType); //设置返回结果格式

        Map<String,String> headersMap = response.getHeaders();
        if(ToolsKit.isNotEmpty(headersMap)) {
            for(Iterator<Map.Entry<String,String>> iterator = headersMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if(ToolsKit.isNotEmpty(key) && ToolsKit.isNotEmpty(value)) {
                    responseHeaders.set(key, value);
                }
            }
        }


        // 数据分块返回客户端，不能与CONTENT_LENGTH同时使用，一般用于图片或文件之类的stream
        // 功能待实现，暂不开启，如开启则返回数据会不显示
        /*
        String acceptEncoding = request.getHeader(HttpHeaderNames.ACCEPT_ENCODING.toString());
        if(ToolsKit.isNotEmpty( acceptEncoding)) {
            responseHeaders.set(HttpHeaderNames.CONTENT_ENCODING.toString(), acceptEncoding);
            responseHeaders.set(HttpHeaderNames.TRANSFER_ENCODING.toString(), HttpHeaderValues.CHUNKED.toString());
        }
        */
        responseHeaders.set(HttpHeaderNames.DATE.toString(), ToolsKit.getCurrentDateString());
        int readableBytesLength = 0;
        try {
            readableBytesLength = fullHttpResponse.content().readableBytes();
        } catch (Exception e) {}
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), readableBytesLength);
    }

}
