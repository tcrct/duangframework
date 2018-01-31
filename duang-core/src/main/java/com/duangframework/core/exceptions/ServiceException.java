package com.duangframework.core.exceptions;

import com.duangframework.core.common.enums.IEnums;

/**
 *
 * @author laotang
 * @date 2017/11/2
 */
public class ServiceException extends RuntimeException {

    private int code = IEnums.IENUMS_FAIL_CODE;
    private String message = IEnums.IENUMS_FAIL_MESSAGE;
    private IEnums enums;

    public ServiceException() {
        super();
    }

    public ServiceException(String msg) {
        super(msg);
        setMessage(msg);
    }

    public ServiceException(String msg , Throwable cause) {
        super(msg, cause);
        setMessage(msg);
    }

    public ServiceException setCode(int code) {
        this.code = code;
        return this;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public ServiceException setIEnums(IEnums enums) {
        this.enums = enums;
        return this;
    }

    public ServiceException(IEnums enums) {
        this.message = enums.getMessage();
        this.enums = enums;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public IEnums getEnums() {
        return enums;
    }

}