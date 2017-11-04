package com.duangframework.core.common.dto.http.request;

import com.duangframework.core.exceptions.EmptyNullException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class RequestWrapper implements IRequest {

    private IRequest request;

    private String endPoint;
    private Charset charset;
    private String method;
    private String queryString;
    private String uri;
    private Map<String,String> headers;
    private Map<String,String[]> params;
    private byte[] content;
    private Map<String,Object> attributes = new ConcurrentHashMap<>();

    public RequestWrapper(String endPoint, Charset charset, String method, String uri, String queryString, Map<String,String> headers, Map<String,String[]> params, byte[] content) {
        this.endPoint = endPoint;
        this.charset = charset;
        this.method = method;
       this.queryString = queryString;
       this.uri = uri;
       this.headers = headers;
       this.params = params;
       this.content = content;
    }

    private RequestWrapper() {

    }

    public RequestWrapper(IRequest request) {
        if(request == null) {
            throw new EmptyNullException("HttpRequest cannot be null");
        } else {
            this.request = request;
        }
    }

//    public IRequest getRequest() {
//        return this.request;
//    }
//
//    public void setRequest(IRequest request) {
//        if(request == null) {
//            throw new EmptyNullException("HttpRequest cannot be null");
//        } else {
//            this.request = request;
//        }
//    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public Object getAttribute() {
        return attributes;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return  new Vector(attributes.keySet()).elements();
    }

    @Override
    public String getCharacterEncoding() {
        return charset.name();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        charset = Charset.forName(env);
    }

    @Override
    public long getContentLength() {
        return null == content ? 0 : content.length;
    }

    @Override
    public String getContentType() {
        return headers.get("content-type");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getParameter(String name) {
        return params.get(name)[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector(params.keySet()).elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
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
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    // 是否开启SSL, 即HTTPS
    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector(headers.keySet()).elements();
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
        return new StringBuffer(endPoint);
    }
}
