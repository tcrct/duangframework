package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class DecoderException extends RuntimeException {

    public DecoderException() {
        super();
    }

    public DecoderException(String msg) {
        super(msg);
    }

    public DecoderException(String msg , Throwable cause) {
        super(msg, cause);
    }

}