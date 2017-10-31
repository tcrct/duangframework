package com.duangframework.server.netty.decoder;

import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public class GetDecoder extends AbstractDecoder<Map<String, Object>> {

    public GetDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        String url = request.uri();
        url = QueryStringDecoder.decodeComponent(url, HttpConstants.DEFAULT_CHARSET); //先解码
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        Map<String,List<String>> map =  queryStringDecoder.parameters();
        if(ToolsKit.isNotEmpty(map)) {
            for(String key : map.keySet()) {
                paramsMap.put(key, map.get(key).get(0));
            }
        }
        return paramsMap;
    }
}
