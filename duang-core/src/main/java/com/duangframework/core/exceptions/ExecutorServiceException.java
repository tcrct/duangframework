package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ExecutorServiceException extends RuntimeException {

    public ExecutorServiceException() {
        super();
    }

    public ExecutorServiceException(String msg) {
        super(msg);
    }

    public ExecutorServiceException(String msg , Throwable cause) {
        super(msg, cause);
    }

}