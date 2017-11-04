package com.duangframework.server.netty.decoder;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.common.enums.ContentType;
import com.duangframework.server.common.enums.HttpMethod;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by laotang on 2017/10/31.
 */
public class DecoderFactory {


    public static AbstractDecoder create(String method, String contentType, FullHttpRequest request)  throws Exception{
        if(ToolsKit.isEmpty(request)) {
            throw new DecoderException("FullHttpRequest is null");
        }
        if(ToolsKit.isEmpty(method)) {
            throw new DecoderException("method is null");
        }

        AbstractDecoder decoder = null;
        if(HttpMethod.GET.name().equalsIgnoreCase(method)) {
            decoder = new GetDecoder(request);
        }
        else if(HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if(ContentType.JSON.getValue().contains(contentType)) {
                decoder = new JsonDecoder(request);
            }
            else if(ContentType.XML.getValue().contains(contentType)) {

            }
            else if (ContentType.FORM.getValue().contains(contentType)) {
                decoder = new PostDecoder(request.copy());
            }
            else if (ContentType.MULTIPART.getValue().contains(contentType)) {
                decoder = new MultiPartPostDecoder(request.copy());
            }
        }

        return decoder;

    }

}
