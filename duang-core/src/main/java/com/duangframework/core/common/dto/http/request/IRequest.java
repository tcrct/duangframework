package com.duangframework.core.common.dto.http.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

/**
 *  duangframework框架自实现的Request, 模拟实现servlet下的request对象
 *  该对象主要用于duang-mvc与duang-server解耦用
 * @author laotang
 * @date 2017/11/2
 */
public interface IRequest {

    /**
     *
     * @return
     */
    Object getAttribute();

    Enumeration<String> getAttributeNames();

    String getCharacterEncoding();

    void setCharacterEncoding(String env) throws UnsupportedEncodingException;

    long getContentLength();

    String getContentType();

    InputStream getInputStream() throws IOException;

    String getParameter(String name);

    Enumeration<String> getParameterNames();

    String[] getParameterValues(String name);

    Map<String, String[]> getParameterMap();

    String getProtocol();

    String getScheme();

    String getServerName();

    int getServerPort();

    String getRemoteAddr();

    String getRemoteHost();

    void setAttribute(String name, Object o);

    void removeAttribute(String name);

    /**
     * 是否安全请求，如果是HTTPS协议的请求视为安全请求
     * @return
     */
    boolean isSecure();

    /************************************************  HEAD 部份 *************************************************************/

    String getHeader(String name);

    Enumeration<String> getHeaderNames();

    String getMethod();

    String getQueryString();

    String getRequestURI();

    StringBuffer getRequestURL();

}
