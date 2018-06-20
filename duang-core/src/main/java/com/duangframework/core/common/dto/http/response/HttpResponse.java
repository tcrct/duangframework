package com.duangframework.core.common.dto.http.response;

import com.duangframework.core.common.dto.http.request.AsyncContext;
import com.duangframework.core.kit.ToolsKit;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/11/8
 */
public class HttpResponse implements IResponse {

    private Map<String,String> headers;
    private String charset;
    private String contentType;
    private Object returnObj;
    private int contentLength;
    private File downloadFile;
    private boolean isDelete; //下载完成后是否删除文件


    public HttpResponse(Map<String,String> headers, String charset, String contentType) {
        this.headers = headers;
        this.charset = charset;
        this.contentType = contentType;
        this.returnObj = null;
    }

    private HttpResponse() {

    }

    @Override
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String,String> getHeaders() {
        return headers;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public int getStatus() {
        try {
            return Integer.parseInt(headers.get("status"));
        } catch (Exception e) {
            return 500;
        }
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public File getDownloadFile() {
        return  returnObj instanceof File ? (File)returnObj : null;
    }

    @Override
    public void write(Object returnObj) {
        this.returnObj = returnObj;
        // 请求超时会是null值
        if(null != _asyncContext) {
            _asyncContext.write(this);
        }
    }

    @Override
    public String toString() {
        if(null != returnObj) {
            return ToolsKit.toJsonString(returnObj);
        }
        return "{\"hello\" : \"duangframework\"}";
    }


    /**
     *
     */
    private AsyncContext _asyncContext;
    @Override
    public void setAsyncContext(AsyncContext asyncContext) {
        this._asyncContext = asyncContext;
    }

    @Override
    public boolean isDeleteDownloadFile() {
        return isDelete;
    }

    @Override
    public void setDeleteDownloadFile(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
