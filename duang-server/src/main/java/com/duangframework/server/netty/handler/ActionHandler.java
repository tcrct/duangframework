package com.duangframework.server.netty.handler;

import com.duangframework.core.exceptions.DecoderException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.server.utils.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ActionHandler extends AbstractHttpHandler implements Runnable{

    private ChannelHandlerContext ctx;
    private FullHttpRequest request;

    public ActionHandler(ChannelHandlerContext ctx, FullHttpRequest request){
        this.ctx = ctx;
        RequestUtils.convertDuangRequest(request);
        this.request = request.copy();
//        decoder(request);
    }

    private Map<String,Object> decoder(FullHttpRequest request) {
        try {
            AbstractDecoder<Map<String, Object>> decoder = DecoderFactory.create(request.method().toString(), request.headers().get(CONTENT_TYPE)+"", request);
            return decoder.decoder();
        } catch (Exception e) {
            throw new DecoderException(e.getMessage(), e);
        }

    }


    @Override
    public void run() {
        try {
            AbstractDecoder<Map<String, Object>> decoder = DecoderFactory.create(request.method().toString(), request.headers().get(CONTENT_TYPE)+"", request);
            Map<String, Object> paramMap = decoder.decoder();
            String body = "";
            Map<String, String > headers = new HashMap<>();
            if(ToolsKit.isNotEmpty(paramMap)) {
                body = ToolsKit.toJsonString(paramMap);
                System.out.println(Thread.currentThread().getId() +"                "+body);
            }
            response(ctx, request, body, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
