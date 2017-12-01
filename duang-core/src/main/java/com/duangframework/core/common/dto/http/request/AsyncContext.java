package com.duangframework.core.common.dto.http.request;

import com.duangframework.core.common.dto.http.response.IResponse;

/**
 * @author Created by laotang
 * @date on 2017/11/28.
 */
public interface AsyncContext {

    void setTimeout(long timeout);

    void write(IResponse response);

    IResponse complete();

    IRequest getAsyncRequest();

    IResponse getAsyncResponse();

}
