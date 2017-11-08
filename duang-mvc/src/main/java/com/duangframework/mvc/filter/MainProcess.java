package com.duangframework.mvc.filter;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServletException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.IProcess;
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
    public void doWork(final IRequest request, final IResponse response) throws Exception {
        if(ToolsKit.isEmpty(request)) {  throw new EmptyNullException("request is null");}
        if(ToolsKit.isEmpty(response)) {throw new EmptyNullException("response is null");}

//        ThreadPoolKit.execute(new Callable<IResponse>() {
//            @Override
//            public IResponse call() throws Exception {
//                return null;
//            }
//        });

        String target = request.getRequestURI();
        ReturnDto returnDto = new ReturnDto<>();
        HeadDto head = new HeadDto();
        head.setTimestamp(System.currentTimeMillis());
        head.setUri(target);
        returnDto.setHead(head);
        returnDto.setData(request.getParameterMap());
        response.write(returnDto);

    }

    @Override
    public void destroy() {

    }
}
