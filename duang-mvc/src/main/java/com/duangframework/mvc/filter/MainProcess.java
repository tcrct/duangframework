package com.duangframework.mvc.filter;

import com.duangframework.core.common.dto.http.request.AsyncContext;
import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.IProcess;
import com.duangframework.mvc.handles.Handles;
import com.duangframework.mvc.kit.MvcConfigKit;
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
                _mainProcess.init(null);
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
    public void init(MvcConfigKit mvcConfig) throws DuangMvcException {
        Handles.init();
    }

    @Override
    public IResponse doWork(final IRequest req, final IResponse res) throws Exception {
        final HttpRequest request = (HttpRequest)req;
//        final HttpResponse response = (HttpResponse)res;
        if(ToolsKit.isEmpty(req)) {  throw new EmptyNullException("request is null");}
        if(ToolsKit.isEmpty(res)) {throw new EmptyNullException("response is null");}

        final AsyncContext asyncContext = request.startAsync(req, res);
        try {
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Handles.execute(request.getRequestURI().toString(), asyncContext.getAsyncRequest(), asyncContext.getAsyncResponse());
                    } catch (Exception e) {
                        throw new DuangMvcException(e.getMessage(), e);
                    }
                }
            });
            return asyncContext.complete();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void destroy() {

    }
}
