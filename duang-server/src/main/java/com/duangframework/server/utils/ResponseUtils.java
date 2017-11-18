package com.duangframework.server.utils;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.http.response.IResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laotang on 2017/11/8.
 */
public class ResponseUtils {

    /**
     * 构建Response对象
     * @param request
     * @return
     */
    public static IResponse buildDuangResponse(IRequest request) {
        Map<String, String > headers = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }
        return new HttpResponse(headers, request.getCharacterEncoding(), request.getContentType());
    }
}
