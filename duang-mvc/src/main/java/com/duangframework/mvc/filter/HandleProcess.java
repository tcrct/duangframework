package com.duangframework.mvc.filter;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.mvc.handles.Handles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

/**
 * @author Created by laotang
 * @date on 2017/11/29.
 */
public class HandleProcess implements Callable<IResponse> {

    private static final Logger logger = LoggerFactory.getLogger(HandleProcess.class);

    private String target;
    private IRequest request;
    private IResponse response;

    HandleProcess(String target, IRequest request, IResponse response) {
        this.target = target;
        this.request = request;
        this.response = response;
    }

    @Override
    public IResponse call() {
        try {
            Handles.execute(target, request, response);
            return response;
        } catch (ServiceException se) {
            throw se;
        } catch (DuangMvcException dme) {
            throw dme;
        } catch (InvocationTargetException ite) {
            throw new DuangMvcException(ite.getCause().getMessage(), ite.getCause());
        } catch (Exception e) {
//            InvocationTargetException ite = (InvocationTargetException)e;
            throw new DuangMvcException(e.getMessage(), e);
        }
    }
}
