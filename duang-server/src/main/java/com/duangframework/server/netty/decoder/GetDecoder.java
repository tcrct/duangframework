package com.duangframework.server.netty.decoder;

import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public class GetDecoder extends AbstractDecoder<Map<String, String[]>> {

    public GetDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, String[]> decoder() throws Exception {
        String url = request.uri();
        //先解码
        url = QueryStringDecoder.decodeComponent(url, HttpConstants.DEFAULT_CHARSET);
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        Map<String,List<String>> map =  queryStringDecoder.parameters();
        if(ToolsKit.isNotEmpty(map)) {
            for(String key : map.keySet()) {
//                System.out.println(key+"               "+map.get(key).toArray(EMPTY_ARRAYS).getClass());
                paramsMap.put(key, map.get(key).toArray(EMPTY_ARRAYS));
            }
        }
        return paramsMap;
    }

    private String sanitizeUri(String url) {
        try {
            url = URLDecoder.decode(url, CharsetUtil.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            try {
                url = URLDecoder.decode(url, CharsetUtil.ISO_8859_1.name());
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException(e1);
            }
        }
        return url;
    }
}
