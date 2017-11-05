package com.duangframework.server.netty.decoder;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class PostDecoder extends AbstractDecoder<Map<String,String[]>> {

    public PostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, String[]> decoder() throws Exception {
        HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            Map<String, List<String>> params = new HashMap<>();
            for (InterfaceHttpData httpData : paramsList) {
                Attribute attribute = (Attribute) httpData;
                System.out.println("###########PostDecoder: "  + attribute.getHttpDataType().getClass().getName());
                String key = attribute.getName();
                String value = attribute.getValue();
                parseValue2List(params, key, value);
                paramsMap.put(key, params.get(key).toArray(EMPTY_ARRAYS));
            }
        }
        return paramsMap;
    }
}
