package com.duangframework.mvc.core;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.mvc.kit.MvcConfigKit;

/**
 * Created by laotang on 2017/11/5.
 */
public interface IProcess {

    /**
     * 初始化
     * @param mvcConfig
     * @throws DuangMvcException
     */
    void init(MvcConfigKit mvcConfig) throws DuangMvcException;

    /**
     *
     */
    IResponse doWork(IRequest request, IResponse response) throws Exception;

    /**
     *
     */
    void destroy();

}
