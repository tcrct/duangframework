package com.duangframework.rule.core;

/**
 * @author Created by laotang
 * @date createed in 2018/1/26.
 */
public enum RuleOperatorEnum {

    EQ("=="),
    NE("!="),
    GT(">"),
    GTE(">="),
    LT ("<"),
    LTE("<=");

    private final String value;
    private RuleOperatorEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
