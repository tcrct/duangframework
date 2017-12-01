package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ProxyChainException extends RuntimeException {

    public ProxyChainException() {
        super();
    }

    public ProxyChainException(String msg) {
        super(msg);
    }

    public ProxyChainException(String msg , Throwable cause) {
        super(msg, cause);
    }

}