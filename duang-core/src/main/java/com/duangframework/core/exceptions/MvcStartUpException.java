package com.duangframework.core.exceptions;

/**
 *  MVC框架启动时异常
 * @author laotang
 * @date 2017/11/2
 */
public class MvcStartUpException extends RuntimeException {

    public MvcStartUpException() {
        super();
    }

    public MvcStartUpException(String msg) {
        super(msg);
    }

    public MvcStartUpException(String msg , Throwable cause) {
        super(msg, cause);
    }

}