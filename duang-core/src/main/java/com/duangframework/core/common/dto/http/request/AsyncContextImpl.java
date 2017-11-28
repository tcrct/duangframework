package com.duangframework.core.common.dto.http.request;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Created by laotang
 * @date on 2017/11/27.
 */
public class AsyncContextImpl implements AsyncContext {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContextImpl.class);

    private IRequest asyncRequest;
    private IResponse asyncResponse;
    private String requestId;
    private long timeout = Const.REQUEST_TIMEOUT;
    public static ConcurrentHashMap<String, LinkedBlockingQueue<IResponse>> RESPONSE_MAP = new ConcurrentHashMap<>();

    public AsyncContextImpl(IRequest request, IResponse response) throws IllegalStateException {
        asyncRequest = request;
        asyncResponse = response;
        init();
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public HttpRequest getAsyncRequest() {
        return (HttpRequest)asyncRequest;
    }

    @Override
    public HttpResponse getAsyncResponse() {
        return (HttpResponse)asyncResponse;
    }

    private void init() {
        requestId = new DuangId().toString();
        asyncRequest.setAttribute(Const.DUANG_REQUEST_ID, requestId);
        RESPONSE_MAP.put(requestId, new LinkedBlockingQueue<IResponse>(1));
    }

    @Override
    public void write(IResponse response) {
        try {
            if(RESPONSE_MAP.containsKey(requestId)) {
                RESPONSE_MAP.get(requestId).put(response);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public IResponse complete() {
        IResponse response = null;
        try {
            response = RESPONSE_MAP.get(requestId).poll(timeout, TimeUnit.MILLISECONDS);
            if(null == response) {
                response = buildTimeOutResponse();
                System.out.println(asyncResponse.hashCode() + "       ##################clear response");
                asyncResponse = null;  //将对象设置为null, 当后续代码继续使用时，则会抛出异常真正结束请求。
                System.out.println(asyncResponse);
            }
            System.out.println(requestId+"                     "+timeout + " #############complete:  " + response.hashCode());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            RESPONSE_MAP.remove(requestId);
        }
        return response;
    }

    private IResponse buildTimeOutResponse() {
        HttpResponse httpResponse = new HttpResponse(asyncResponse.getHeaders(), asyncResponse.getCharacterEncoding(), asyncRequest.getContentType());
        ReturnDto<String> returnDto = new ReturnDto<>();
        returnDto.setData("request is time out");
        HeadDto headDto = new HeadDto();
        headDto.setMsg("time out");
        headDto.setUri(asyncRequest.getRequestURI());
        headDto.setRet(1);
        headDto.setTimestamp(System.currentTimeMillis());
        headDto.setRequestId(requestId);
        headDto.setClientId(IpUtils.getLocalHostIP());
        headDto.setMethod(asyncRequest.getMethod());
        returnDto.setHead(headDto);
        httpResponse.write(returnDto);
        return httpResponse;
    }

}
