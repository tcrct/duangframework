package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.exceptions.DecoderException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.server.utils.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderUtil;

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
//    private FullHttpRequest request;
    private IRequest request;
    private boolean keepAlive; //是否支持Keep-Alive

    public ActionHandler(ChannelHandlerContext ctx, FullHttpRequest request){
        this.ctx = ctx;
        this.keepAlive = HttpHeaderUtil.isKeepAlive(request);
        this.request = RequestUtils.convertDuangRequest(request);
//        this.request = request.copy();
//        decoder(request);
    }

//    private Map<String,Object> decoder(FullHttpRequest request) {
//        try {
//            AbstractDecoder<Map<String, Object>> decoder = DecoderFactory.create(request.method().toString(), request.headers().get(CONTENT_TYPE)+"", request);
//            return decoder.decoder();
//        } catch (Exception e) {
//            throw new DecoderException(e.getMessage(), e);
//        }
//    }


    @Override
    public void run() {
        try {
            String body = "";
            Map<String, String > headers = new HashMap<>();
            if(ToolsKit.isNotEmpty(request.getParameterMap())) {
                body = ToolsKit.toJsonString(request.getParameterMap());
                System.out.println(Thread.currentThread().getId() +"                "+body);
            }
            response(ctx, keepAlive, body, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
