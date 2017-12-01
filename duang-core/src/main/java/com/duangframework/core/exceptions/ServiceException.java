package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
        super();
    }

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg , Throwable cause) {
        super(msg, cause);
    }

}