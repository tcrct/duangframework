package com.duangframework.server.netty.decoder;

import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class MultiPartPostDecoder extends AbstractDecoder<Map<String,String[]>> {

    public MultiPartPostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, String[]> decoder() throws Exception {
        HttpPostMultipartRequestDecoder requestDecoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            Map<String, List<String>> params = new HashMap<>();
            for (InterfaceHttpData httpData : paramsList) {
                MemoryAttribute attribute = (MemoryAttribute) httpData;
                String key = attribute.getName();
                String value = attribute.getValue();
                List<String> list = params.get(key);
                if(ToolsKit.isEmpty(value)) {
                    continue;
                }
                parseValue2List(params, key, value);
                paramsMap.put(key, list.toArray(EMPTY_ARRAYS));
            }
        }
        return paramsMap;
    }
}
