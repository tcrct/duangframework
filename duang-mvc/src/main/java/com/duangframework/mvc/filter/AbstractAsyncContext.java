package com.duangframework.mvc.filter;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.dto.http.request.AsyncContext;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.InstanceFactory;

/**
 * @author Created by laotang
 * @date on 2017/11/29.
 */
public abstract class AbstractAsyncContext implements AsyncContext {

    private IRequest asyncRequest;
    private IResponse asyncResponse;
    private String target;
    private String requestId;
    private long timeout = 3000L;

    public AbstractAsyncContext(String target, IRequest request, IResponse response) {
        this(target, request, response, Const.REQUEST_TIMEOUT);
    }

    public AbstractAsyncContext(String target, IRequest request, IResponse response, long timeout)  {
        this.target = target;
        this.asyncRequest = request;
        this.asyncResponse = response;
        this.timeout = timeout;
        init();
    }

    public String getTarget() {
        return target;
    }

    @Override
    public long getTimeout() {
        Action action = InstanceFactory.getActionMapping().get(target);
        if(ToolsKit.isNotEmpty(action)) {
            return action.getTimeout();
        }
        return timeout;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public IRequest getAsyncRequest() {
        return asyncRequest;
    }

    @Override
    public IResponse getAsyncResponse() {
        return asyncResponse;
    }

    protected void setAsyncResponse(IResponse response) {
        asyncResponse = response;
    }

    private void init() {
        requestId = new DuangId().toString();
        asyncRequest.setAttribute(Const.DUANG_REQUEST_ID, requestId);
    }

    protected IResponse buildExceptionResponse(String message) {
        HttpResponse httpResponse = new HttpResponse(asyncResponse.getHeaders(), asyncResponse.getCharacterEncoding(), asyncRequest.getContentType());
        ReturnDto<String> returnDto = new ReturnDto<>();
        returnDto.setData(null);
        HeadDto headDto = new HeadDto();
        int index = message.indexOf(":");
        headDto.setMsg((index>-1) ? message.substring(index+1, message.length()) : message);
        headDto.setRet(1);
        headDto.setUri(asyncRequest.getRequestURI());
        headDto.setTimestamp(System.currentTimeMillis());
        headDto.setRequestId(requestId);
        headDto.setClientId(IpUtils.getLocalHostIP());
        headDto.setMethod(asyncRequest.getMethod());
        returnDto.setHead(headDto);
        httpResponse.write(returnDto);
        httpResponse.setHeader("status", (headDto.getRet() == 0) ? "200" : "500");
        return httpResponse;
    }

    @Override
    public void write(IResponse response) {

    }
}
