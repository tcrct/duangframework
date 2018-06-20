package com.duangframework.core.common.dto.http.response;

import com.duangframework.core.common.dto.http.request.AsyncContext;

import java.io.File;
import java.util.Collection;
import java.util.Map;

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
     * 添加返回头信息
     * @param key           名称
     * @param value         值
     */
    void setHeader(String key, String value);

    /**
     * 根据name返回头信息
     * @param name          名称
     */
    String getHeader(String name);

    /**
     * 返回Header头所有名称
     * @return
     */
    Collection<String> getHeaderNames();

    /**
     * 返回header对象
     * @return
     */
    Map<String,String> getHeaders();

    /**
     * 取返回状态标识
     * @return
     */
    int getStatus();

    /**
     * 设置编码格式
     * @return
     */
    void setCharacterEncoding(String charset);

    /**
     * 返回编码格式名称
     * @return
     */
    String getCharacterEncoding();

    /**
     * 设置返回ContentType信息
     * @param contentType
     */
    void setContentType(String contentType);

    /**
     * 设置返回主体内容
     * @param returnObj     返回主体对象
     */
    void write(Object returnObj);

    @Override
    String toString();

    /**
     *
     * @param asyncContext
     */
    void setAsyncContext(AsyncContext asyncContext);

    /**
     *
     * @param contentLength
     */
    void setContentLength(int contentLength);

    /**
     *
     * @return
     */
    File getDownloadFile();

    /**
     * 是否删除下载文件
     * @return
     */
    boolean isDeleteDownloadFile();

    /**
     * 是否删除下载文件
     * @return
     */
    void setDeleteDownloadFile(boolean isDelete);

}
