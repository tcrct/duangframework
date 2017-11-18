package com.duangframework.core.interfaces;

import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;

/**
 * 处理器接口
 * @author laotang
 * @date 2017/11/8
 */
public interface IHandle {

	void execute(String target, HttpRequest request, HttpResponse response) throws Exception;

}
