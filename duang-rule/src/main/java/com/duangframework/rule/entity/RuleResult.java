package com.duangframework.rule.entity;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleResult {

    private int code;
    private String message;

    public RuleResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RuleResult() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}
