package com.duangframework.server.netty.handler;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.common.enums.HttpMethod;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.multipart.DefaultHttpDataFactory.MINSIZE;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class HttpServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(HttpServiceHandler.class);

    private static final String JSON = new AsciiString("application/json; charset=utf-8").toString();

    private static HttpDataFactory httpDataFactory = new DefaultHttpDataFactory(MINSIZE, HttpConstants.DEFAULT_CHARSET);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        // 保证解析结果正确,否则直接退出
        if (!request.decoderResult().isSuccess()) {
            logger.warn("request decoder is not success, so exit...");
            return;
        }

        // 支持的的请求方式
        String method = request.method().toString();
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        if(ToolsKit.isEmpty(httpMethod)) {
            logger.warn("request method["+ httpMethod.toString() +"] is not support, so exit...");
            return ;
        }

        // uri是有长度的
        final String uri = request.uri();
        if (uri == null || uri.trim().length() == 0) {
            logger.warn("request uri length is 0 , so exit...");
            return;
        }

        // 如果包含有.则视为静态文件访问
        if(uri.contains(".")) {
            logger.warn("not support static file access, so exit...");
            return;
        }

        Map<String,String> headerParmasMap = new ConcurrentHashMap<String,String>();
        HttpHeaders httpHeaders = request.headers();
        if(null != httpHeaders && !httpHeaders.isEmpty()) {
            for (Iterator<Map.Entry<String, String>> it = httpHeaders.iteratorConverted(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                headerParmasMap.put(entry.getKey(), entry.getValue());
            }
        }

        String resultString = "";

        String contentType = httpHeaders.get(CONTENT_TYPE).toString();

        AbstractDecoder<Map<String,Object>> decoder = DecoderFactory.create(method, contentType, request);
        Map<String,Object> paramMap = decoder.decoder();

        if(ToolsKit.isNotEmpty(paramMap)) {
            resultString = ToolsKit.toJsonString(paramMap);
            System.out.println(resultString);
        }

        // 是否支持Keep-Alive
        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
        // 设置返回结果
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(resultString, HttpConstants.DEFAULT_CHARSET));
        response.headers().setAll(httpHeaders);
        response.headers().set(CONTENT_TYPE, JSON); //设置默认的返回结果
        response.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        response.headers().set(HttpHeaderNames.DATE, ToolsKit.getCurrentDateString());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()+"");
        HttpHeaderUtil.setKeepAlive(response, keepAlive);
        ChannelFuture channelFutureListener = ctx.writeAndFlush(response);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if(!keepAlive) {
            channelFutureListener.addListener(ChannelFutureListener.CLOSE);
        }

//        new NettyMainFilter(ctx, request, response);
    }
}
