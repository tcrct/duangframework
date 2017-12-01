package com.duangframework.core.interfaces;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;

/**
 * 处理器接口
 * @author laotang
 * @date 2017/11/8
 */
public interface IHandle {

	void execute(String target, IRequest request, IResponse response) throws Exception;

}
