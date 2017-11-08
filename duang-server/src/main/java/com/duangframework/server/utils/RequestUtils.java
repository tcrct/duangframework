package com.duangframework.server.utils;

import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.request.RequestWrapper;
import com.duangframework.core.exceptions.DecoderException;
import com.duangframework.core.exceptions.VerificationException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.common.enums.HttpMethod;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.server.netty.server.BootStrap;
import com.duangframework.server.netty.server.ServerConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 *
 * @author  laotang
 * @date 2017/11/4.
 */
public class RequestUtils {

    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * 将netty的FullHttpRequest转换为duang-mvc模块所需要的Request对象
     * 以致duang-mvc不依赖于netty
     *
     * @param request  FullHttpRequest对象
     * @return
     */
    public static IRequest buildDuangRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            // 装饰模式
            RequestWrapper httpWrapper = new RequestWrapper(
                    getRemoteAddr(ctx.channel(), request),
                    getLocalAddr(request),
                    getHeaders(request),
                    decoder(request),
                    //Unpooled.wrappedBuffer(request.content()).array(),
                Unpooled.copiedBuffer(request.content()).array()
            );
            return new HttpRequest(httpWrapper).getRequest();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    private static Map<String,String> getHeaders(FullHttpRequest request) {
        Map<String,String> headerMap = null;
        HttpHeaders headers = request.headers();
        if(ToolsKit.isNotEmpty(headers)) {
            headerMap = new HashMap<>(headers.size());
            for(Iterator<Map.Entry<String,String>> it = headers.iteratorConverted(); it.hasNext();) {
                Map.Entry<String,String> entry = it.next();
                headerMap.put(entry.getKey(), entry.getValue());
            }
        }
        return headerMap;
    }

    private static Map<String,String[]> decoder(FullHttpRequest request) {
        try {
            AbstractDecoder<Map<String, String[]>> decoder = DecoderFactory.create(request.method().toString(), request.headers().get(CONTENT_TYPE)+"", request);
            return decoder.decoder();
        } catch (Exception e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    /**
     * 验证请求是否正确
     * @return
     */
    public static void verificationRequest(FullHttpRequest request) {

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

    /**
     * 取客户端IP地址
     * @param channel
     * @param request
     * @return
     */
    private static URI getRemoteAddr(Channel channel, FullHttpRequest request) throws Exception {
        String ipAddress = "";
        int paranPort = 80;     //PID
        try {
            ipAddress = request.headers().get(ServerConfig.HEADER_X_FORWARDED_FOR)+"";
            if (ToolsKit.isEmpty(ipAddress) || ServerConfig.UNKNOWN.equalsIgnoreCase(ipAddress)) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                paranPort = inetSocketAddress.getPort(); //PID
                InetAddress inetAddress = inetSocketAddress.getAddress();
                ipAddress = inetAddress.getHostAddress()+":"+paranPort;
            }
        } catch (Exception e) {
            logger.warn("getRemoteAddr(): get remote ip fail: " + e.getMessage(), e);
        }
        if("0:0:0:0:0:0:0:1".equals(ipAddress) || ToolsKit.isEmpty(ipAddress)){
            ipAddress = "127.0.0.1";
        }

        String protocolStr = request.protocolVersion().protocolName().toString().toLowerCase();
        String endPoint = protocolStr + "://" + ipAddress + request.uri();
        return new URI(endPoint);
    }

    /**
     * 取服务器P地址
     * @param request
     * @return
     */
    private static URI getLocalAddr(FullHttpRequest request)  throws Exception {
        String protocolStr = request.protocolVersion().protocolName().toString().toLowerCase();
        InetSocketAddress inetSocketAddress = BootStrap.getInstants().getSockerAddress();
        String endPoint = protocolStr+"://"+inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
        return new URI(endPoint);
    }
}
