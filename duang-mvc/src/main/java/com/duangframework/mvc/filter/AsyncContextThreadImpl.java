package com.duangframework.mvc.filter;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用ExecutorService线程池自带的等待线程方式控制请求超时
 *
 * @author Created by laotang
 * @date on 2017/11/27.
 */
public class AsyncContextThreadImpl extends AbstractAsyncContext {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContextThreadImpl.class);


    public AsyncContextThreadImpl(String target, IRequest request, IResponse response) throws Exception {
        this(target, request, response, Const.REQUEST_TIMEOUT);
    }

    public AsyncContextThreadImpl(String target, IRequest request, IResponse response, long timeout) throws Exception {
        super(target, request, response, timeout);
    }

    /**
     *  异步执行完成
     * @return
     */
    @Override
    public IResponse complete() {
        HandleProcess handleProcess = null;
        IResponse response = null;
        FutureTask<IResponse> futureTask = null;
        try {
            // 创建请求处理器线程
            handleProcess = new HandleProcess(getTarget(), getAsyncRequest(), getAsyncResponse());
            // 线程池方式执行请求处理器链
            futureTask = ThreadPoolKit.execute(handleProcess);
            // 是否开发模式，如果是则不指定超时
            if(ToolsKit.isDebug()) {
                response = futureTask.get();
            } else {
                // 等待结果返回，如果超出指定时间，则抛出TimeoutException, 默认时间为3秒
                response = futureTask.get(getTimeout(), TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            // 超时时，会执行该异常
            response = buildExceptionResponse("request time out");
            // 中止线程，参数为true时，会中止正在运行的线程，为false时，如果线程未开始，则停止运行
            futureTask.cancel(true);
        }
        catch (DuangMvcException e) {
            logger.warn(e.getMessage(),e);
            response = buildExceptionResponse(e.getMessage());
        }
        catch (RuntimeException  e) {
            logger.warn(e.getMessage(),e);
            response = buildExceptionResponse(e.getMessage());
        }
        catch (Throwable  e) {
            logger.warn(e.getMessage(),e);
            response = buildExceptionResponse(e.getMessage());
        }
        return response;
    }
}


