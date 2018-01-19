package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.server.BootStrap;
import com.duangframework.server.utils.RequestUtils;
import com.duangframework.server.utils.ResponseUtils;
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
    private BootStrap bootStrap;

    public  HttpBaseHandler(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        try {
            RequestUtils.verificationRequest(request);
            new ActionHandler(bootStrap, ctx, request).run();
        }catch (VerificationException ve) {
            logger.warn(ve.getMessage());
            ReturnDto<String> returnDto = new ReturnDto<>();
            HeadDto headDto = new HeadDto();
            headDto.setUri(request.uri());
            headDto.setTimestamp(System.currentTimeMillis());
            headDto.setRet(500);
            headDto.setMsg(ve.getMessage());
            returnDto.setData(ve.getMessage());
            returnDto.setHead(headDto);
            ResponseUtils.buildFullHttpResponse(ctx, request, ToolsKit.toJsonString(returnDto));
        }
    }
}
