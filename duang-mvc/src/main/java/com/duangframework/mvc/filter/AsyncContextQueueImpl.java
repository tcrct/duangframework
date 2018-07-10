package com.duangframework.mvc.filter;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.kit.ThreadPoolKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 用回调+队列方式控制请求超时
 * 该方案作为备用方案，用于了解业务规则与测试
 * @author Created by laotang
 * @date on 2017/11/27.
 */
public class AsyncContextQueueImpl extends AbstractAsyncContext {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContextQueueImpl.class);

    public static ConcurrentHashMap<String, LinkedBlockingQueue<IResponse>> RESPONSE_MAP = new ConcurrentHashMap<>();

    public AsyncContextQueueImpl(String target, IRequest request, IResponse response) throws IllegalStateException {
        this(target, request, response, Const.REQUEST_TIMEOUT);
    }

    public AsyncContextQueueImpl(String target, IRequest request, IResponse response, long timeout) throws IllegalStateException {
        super(target, request, response, timeout);
        init2();
    }

    private void init2() {
        // 将tAsyncContext设置到Request对象，再设置到Response对象
        ((HttpRequest)getAsyncRequest()).startAsync(this);
        // 将该请求放入到Map中，key为请求id，value为链表式队列
        RESPONSE_MAP.put(getRequestId(), new LinkedBlockingQueue<IResponse>(1));
    }

    /**
     * 回调方法，当response执行write方法后，会回调此方法，将结果压入到Map中
     * @param response
     */
    @Override
    public void write(IResponse response) {
        try {
            if(RESPONSE_MAP.containsKey(getRequestId())) {
                RESPONSE_MAP.get(getRequestId()).put(response);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public IResponse complete() {
        IResponse response = null;
        try {
            // 创建请求处理器线程
            HandleProcess handleProcess = new HandleProcess(getTarget(), getAsyncRequest(), getAsyncResponse());
            // 线程池方式执行请求处理器链
            ThreadPoolKit.execute(handleProcess);
            // 等待结果返回，如果超出指定时间，默认时间为3秒
            response = RESPONSE_MAP.get(getRequestId()).poll(getTimeout(), TimeUnit.MILLISECONDS);
            // 超时则创建一个异常response
            if(null == response) {
                response = buildExceptionResponse("request time out",1);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            RESPONSE_MAP.remove(getRequestId());
        }
        return response;
    }



}
