package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.exceptions.DecoderException;
import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.server.utils.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ActionHandler extends AbstractHttpHandler implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(ActionHandler.class);

    private ChannelHandlerContext ctx;
//    private FullHttpRequest request;
    private IRequest request;
    private boolean keepAlive; //是否支持Keep-Alive

    public ActionHandler(ChannelHandlerContext ctx, FullHttpRequest request){
        this.ctx = ctx;
        this.keepAlive = HttpHeaderUtil.isKeepAlive(request);
        this.request = RequestUtils.convertDuangRequest(ctx, request);
    }

    @Override
    public void run() {
        try {
            String body = "";
            Map<String, String > headers = new HashMap<>();
            if(ToolsKit.isNotEmpty(request.getParameterMap())) {
                body = ToolsKit.toJsonString(request);
            }
            response(ctx, keepAlive, body, headers);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
