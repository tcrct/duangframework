package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ServletException extends RuntimeException {

    public ServletException() {
        super();
    }

    public ServletException(String msg) {
        super(msg);
    }

    public ServletException(String msg , Throwable cause) {
        super(msg, cause);
    }

}