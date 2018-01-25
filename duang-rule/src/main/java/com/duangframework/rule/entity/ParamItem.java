package com.duangframework.rule.entity;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class ParamItem<T> {

    /**
     * 要验证规则的字段名称
     */
    private String key;
    /**
     *  要验证规则的内容值
     */
    private T value;

    public ParamItem() {
    }

    public ParamItem(String key, T value) {
        this.key = key;
        this.value = value;
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
