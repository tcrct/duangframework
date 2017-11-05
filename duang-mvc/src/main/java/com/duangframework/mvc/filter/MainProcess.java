package com.duangframework.mvc.filter;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.ServletException;
import com.duangframework.mvc.core.IProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    public void init(MvcConfig mvcConfig) throws ServletException {

    }

    @Override
    public void doWork(IRequest request, IResponse response) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
