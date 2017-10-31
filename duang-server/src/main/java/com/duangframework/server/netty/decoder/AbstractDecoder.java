package com.duangframework.server.netty.decoder;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.multipart.DefaultHttpDataFactory.MINSIZE;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public abstract class AbstractDecoder<T> {

    protected static HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(MINSIZE, HttpConstants.DEFAULT_CHARSET);

    protected FullHttpRequest request;
    protected  Map<String,Object> paramsMap;

    public AbstractDecoder(FullHttpRequest request) {
        this.request = request;
        paramsMap = new ConcurrentHashMap<>();
    }


    public abstract  T decoder() throws Exception;

}
