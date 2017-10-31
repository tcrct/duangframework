package com.duangframework.server.netty.decoder;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class MultiPartPostDecoder extends AbstractDecoder<Map<String,Object>> {

    public MultiPartPostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        HttpPostMultipartRequestDecoder requestDecoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                MemoryAttribute attribute = (MemoryAttribute) httpData;
                paramsMap.put(attribute.getName(), attribute.getValue());
            }
        }
        return paramsMap;
    }
}
