package com.duangframework.core.exceptions;

/**
 *  MVC框架启动时异常
 * @author laotang
 * @date 2017/11/2
 */
public class ServerStartUpException extends RuntimeException {

    public ServerStartUpException() {
        super();
    }

    public ServerStartUpException(String msg) {
        super(msg);
    }

    public ServerStartUpException(String msg , Throwable cause) {
        super(msg, cause);
    }

}