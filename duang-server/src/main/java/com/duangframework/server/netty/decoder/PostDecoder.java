package com.duangframework.server.netty.decoder;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class PostDecoder extends AbstractDecoder<Map<String,Object>> {

    public PostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                Attribute attribute = (Attribute) httpData;
                paramsMap.put(attribute.getName(), attribute.getValue());
            }
        }
        return paramsMap;
    }
}
