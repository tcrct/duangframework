package com.duangframework.mvc.filter;

import com.duangframework.core.common.dto.http.request.AsyncContext;
import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.interfaces.IProcess;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.listener.ContextLoaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 主处理器， 程序的唯一入口点
 * Created by laotang on 2017/11/5.
 */
public class MainProcess implements IProcess {

    private static Lock _lock = new ReentrantLock();
    private static MainProcess _mainProcess;

    private static Logger logger = LoggerFactory.getLogger(MainProcess.class);

    public static MainProcess getInstantiation() {
        if(null == _mainProcess) {
            try {
                _lock.lock();
                _mainProcess = new MainProcess();
                _mainProcess.init();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _lock.unlock();
            }
        }
        return _mainProcess;
    }

    private MainProcess() {

    }

    @Override
    public void init() throws DuangMvcException {
    }

    @Override
    public IResponse doWork(IRequest req, IResponse res) throws Exception {
        HttpRequest request = (HttpRequest)req;
        HttpResponse response = (HttpResponse)res;
        if(ToolsKit.isEmpty(request)) {  throw new EmptyNullException("request is null");}
        if(ToolsKit.isEmpty(response)) {throw new EmptyNullException("response is null");}
        String target = request.getRequestURI().toString();

        AsyncContext asyncContext = new AsyncContextThreadImpl(target, request, response);
        return asyncContext.complete();

//        AsyncContext asyncContext = new AsyncContextQueueImpl(target, request, response);
//        return asyncContext.complete();
    }

    @Override
    public void destroy() {
        ContextLoaderListener.contextDestroyed();
    }
}
