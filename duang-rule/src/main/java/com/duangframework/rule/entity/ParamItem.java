package com.duangframework.rule.entity;

import com.duangframework.rule.core.RuleOperatorEnum;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class ParamItem<T> {

    /**
     * 要验证规则的字段名称(唯一)
     */
    private String key;
    /**
     *  验证规则运算符枚举
     */
    private RuleOperatorEnum operatorEnum;
    /**
     *  要验证规则的内容值
     */
    private T value;

    public ParamItem() {
    }

    public ParamItem(String key, RuleOperatorEnum operatorEnum, T value) {
        this.key = key;
        this.operatorEnum = operatorEnum;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RuleOperatorEnum getOperatorEnum() {
        return operatorEnum;
    }

    public void setOperatorEnum(RuleOperatorEnum operatorEnum) {
        this.operatorEnum = operatorEnum;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
