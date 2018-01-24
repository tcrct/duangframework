package com.duangframework.rule.entity;

/**
 * @author Created by laotang
 * @date createed in 2018/1/24.
 */
public class RuleParam<T> {

    /**
     * 规则名称 <br/>
     * drools规则表drl里的 rule 字段值
     *
     */
    private String ruleName;
    /**
     * 要验证规则的字段名称
     */
    private String key;
    /**
     *  要验证规则的内容值
     */
    private T value;

    public RuleParam() {
    }

    public RuleParam(String ruleName, String key, T value) {
        this.ruleName = ruleName;
        this.key = key;
        this.value = value;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
