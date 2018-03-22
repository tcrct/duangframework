package com.duangframework.server.netty.decoder;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public class OptionsDecoder extends AbstractDecoder<Map<String, String[]>> {

    public OptionsDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, String[]> decoder() throws Exception {
        return paramsMap;
    }
}
