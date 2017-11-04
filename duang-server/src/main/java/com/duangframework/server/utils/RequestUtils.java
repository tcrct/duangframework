package com.duangframework.server.utils;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.request.RequestWrapper;
import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by laotang on 2017/11/4.
 */
public class RequestUtils {

    public static IRequest convertDuangRequest(FullHttpRequest request) {

//        String method = request.method();
//        String queryString = request.uri();

//        Map<String,String> headerMap = getHeaders(request);
//        System.out.println(request.content().toString());

        RequestWrapper httpRequest = new RequestWrapper(request.method().toString(), request.uri(), request.uri(), getHeaders(request));
//        httpRequest.set

        return httpRequest;
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

}
