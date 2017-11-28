package com.duangframework.server.netty.handler;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.mvc.filter.MainProcess;
import com.duangframework.server.utils.RequestUtils;
import com.duangframework.server.utils.ResponseUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ActionThreadHandler extends AbstractHttpHandler implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(ActionThreadHandler.class);

    private ChannelHandlerContext ctx;
//    private FullHttpRequest request;
    private IRequest request;
    private IResponse response;
    private boolean keepAlive; //是否支持Keep-Alive

    public ActionThreadHandler(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest){
        this.ctx = ctx;
        this.keepAlive = HttpHeaderUtil.isKeepAlive(fullHttpRequest);
        this.request = RequestUtils.buildDuangRequest(ctx, fullHttpRequest);
        this.response = ResponseUtils.buildDuangResponse(request);
    }

    @Override
    public void run() {
        if (null == request || null == response) {
            throw new EmptyNullException("build duang request or response is fail, exit...");
        }
        try {
            // TODO 调用MCV模块的主入口方法 如何变得更优雅一点呢？
            // 调用MCV模块的主入口方法
            response = MainProcess.getInstantiation().doWork(request, response);
            // 返回到客户端
            System.out.println("###########SSSSSSSSS###############");
            response(ctx, keepAlive, response);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}