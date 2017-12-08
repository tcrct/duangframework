package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class RpcException extends RuntimeException {

    public RpcException() {
        super();
    }

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String msg , Throwable cause) {
        super(msg, cause);
    }

}