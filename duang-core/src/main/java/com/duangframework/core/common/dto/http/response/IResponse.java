package com.duangframework.core.common.dto.http.response;

import com.duangframework.core.common.dto.result.ReturnDto;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public interface IResponse {

    /**
     * 添加返回头信息
     * @param key           名称
     * @param value         值
     */
    void addHeader(String key, String value);

    /**
     * 设置返回ContentType信息
     * @param contentType
     */
    void setContentType(String contentType);

    /**
     * 设置返回主体内容
     * @param returnDto     返回主体对象
     */
    void write(ReturnDto returnDto);

    /**
     * 关闭清除返回对象
     */
    void close();
}
