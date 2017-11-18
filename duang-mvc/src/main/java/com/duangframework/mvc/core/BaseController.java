package com.duangframework.mvc.core;

import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse2;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by laotang
 * @date on 2017/11/17.
 */
public abstract class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    private HttpRequest request;
    private HttpResponse2 response;

    public void init(HttpRequest request, HttpResponse2 response) {
        this.request = request;
        this.response = response;
        logger.warn(ToolsKit.toJsonString(request.getParameterMap()));
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse2 getResponse() {
        return response;
    }
}
