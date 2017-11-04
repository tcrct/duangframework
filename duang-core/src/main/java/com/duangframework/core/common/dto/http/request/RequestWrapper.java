package com.duangframework.core.common.dto.http.request;

import com.duangframework.core.exceptions.EmptyNullException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class RequestWrapper implements IRequest {

    private IRequest request;
    private String method;
    private String queryString;
    private String uri;
    private Map<String,String> headers;

    public RequestWrapper(String method, String uri, String queryString, Map<String,String> headers) {
       this.method = method;
       this.queryString = queryString;
       this.uri = uri;
       this.headers = headers;
    }

    public IRequest getRequest() {
        return this.request;
    }

    public void setRequest(IRequest request) {
        if(request == null) {
            throw new EmptyNullException("HttpRequest cannot be null");
        } else {
            this.request = request;
        }
    }

    @Override
    public Object getAttribute() {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }
}
