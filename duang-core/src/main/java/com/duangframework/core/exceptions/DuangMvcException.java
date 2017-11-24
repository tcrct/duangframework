package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class DuangMvcException extends RuntimeException {

    public DuangMvcException() {
        super();
    }

    public DuangMvcException(String msg) {
        super(msg);
    }

    public DuangMvcException(String msg , Throwable cause) {
        super(msg, cause);
    }

}