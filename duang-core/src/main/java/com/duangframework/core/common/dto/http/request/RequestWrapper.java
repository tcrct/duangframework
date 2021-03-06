package com.duangframework.core.common.dto.http.request;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;


import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class RequestWrapper implements IRequest {

    private final static String CONTENT_ENCODING = "content-encoding";

    private IRequest request;
    private URI remoteEndPoint;
    private URI localEndPoint;
    private Charset charset;
    private Map<String,String> headers;
    private Map<String,String[]> params;
    private byte[] content;
    private Map<String,Object> attributes = new ConcurrentHashMap<>();

    public RequestWrapper(URI remoteEndPoint, URI localEndPoint, Map<String,String> headers, Map<String,String[]> params, byte[] content) {
        this.remoteEndPoint = remoteEndPoint;
        this.localEndPoint = localEndPoint;
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

    public IRequest getRequest() {
        return this.request;
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return  new Vector(attributes.keySet()).elements();
    }

    @Override
    public String getCharacterEncoding() {
        if(null == charset) {
            String encodering = headers.get(CONTENT_ENCODING);
            if (ToolsKit.isEmpty(encodering)) {
                charset = Charset.defaultCharset();
            } else {
                charset = Charset.forName(encodering);
            }
        }
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
        return remoteEndPoint.getScheme();
    }

    @Override
    public String getScheme() {
        return remoteEndPoint.getScheme();
    }

    @Override
    public String getServerName() {
        return remoteEndPoint.getHost();
    }

    @Override
    public int getServerPort() {
        return localEndPoint.getPort();
    }

    @Override
    public String getRemoteAddr() {
        return remoteEndPoint.getHost();
    }

    @Override
    public String getRemoteHost() {
        return remoteEndPoint.getHost();
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
        return headers.get(name.toLowerCase());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector(headers.keySet()).elements();
    }

    @Override
    public String getMethod() {
        return headers.get("");
    }

    @Override
    public String getQueryString() {
        return remoteEndPoint.getQuery();
    }

    @Override
    public String getRequestURI() {
        return remoteEndPoint.getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        try {
            return new StringBuffer(remoteEndPoint.toURL().toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return headers;
    }
}
