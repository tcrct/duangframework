package com.duangframework.core.exceptions;

/**
 * Created by laotang on 2017/11/2.
 */
public class VerificationException extends RuntimeException {

    public VerificationException() {
        super();
    }

    public VerificationException(String msg) {
        super(msg);
    }

    public VerificationException(String msg , Throwable cause) {
        super(msg, cause);
    }

}
