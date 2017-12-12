package com.duangframework.core.interfaces;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;

/**
 * Created by laotang on 2017/11/5.
 */
public interface IProcess {

    /**
     * 初始化
     * @throws DuangMvcException
     */
    void init() throws DuangMvcException;

    /**
     *
     */
    IResponse doWork(IRequest request, IResponse response) throws Exception;

    /**
     *
     */
    void destroy();

}
