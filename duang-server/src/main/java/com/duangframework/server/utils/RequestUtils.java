package com.duangframework.server.utils;

import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.request.RequestWrapper;
import com.duangframework.core.exceptions.DecoderException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * Created by laotang on 2017/11/4.
 */
public class RequestUtils {

    public static IRequest convertDuangRequest(FullHttpRequest request) {

//        String method = request.method();
        String queryString = request.uri();
        System.out.println(queryString);

//        Map<String,String> headerMap = getHeaders(request);
//        System.out.println(request.content().toString());



        // 装饰模式
        RequestWrapper httpWrapper = new RequestWrapper(
                request.uri(),
                CharsetUtil.UTF_8,
                request.method().toString(),
                request.uri(),
                request.uri(),
                getHeaders(request),
                decoder(request),
//                Unpooled.copiedBuffer(request.content()).array()
                Unpooled.wrappedBuffer(request.content()).array()
                );
        return new HttpRequest(httpWrapper);
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

}
