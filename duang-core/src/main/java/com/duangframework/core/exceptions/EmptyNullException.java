package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class EmptyNullException extends RuntimeException {

    public EmptyNullException() {
        super();
    }

    public EmptyNullException(String msg) {
        super(msg);
    }

    public EmptyNullException(String msg , Throwable cause) {
        super(msg, cause);
    }

}