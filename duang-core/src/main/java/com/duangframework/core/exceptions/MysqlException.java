package com.duangframework.core.exceptions;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class MysqlException extends RuntimeException {

    public MysqlException() {
        super();
    }

    public MysqlException(String msg) {
        super(msg);
    }

    public MysqlException(String msg , Throwable cause) {
        super(msg, cause);
    }

}