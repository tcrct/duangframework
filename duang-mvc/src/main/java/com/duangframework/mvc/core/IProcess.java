package com.duangframework.mvc.core;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.ServletException;
import com.duangframework.mvc.filter.MvcConfig;

import java.io.IOException;

/**
 * Created by laotang on 2017/11/5.
 */
public interface IProcess {

    /**
     * 初始化
     * @param mvcConfig
     * @throws ServletException
     */
    void init(MvcConfig mvcConfig) throws ServletException;

    /**
     *
     */
    void doWork(IRequest request, IResponse response) throws IOException, ServletException;

    /**
     *
     */
    void destroy();

}
