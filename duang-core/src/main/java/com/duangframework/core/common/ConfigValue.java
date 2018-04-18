package com.duangframework.core.common;

import com.duangframework.core.kit.ConfigKit;

/**
 * 键值对象
 */
public class ConfigValue {

    private String key;
    private Object value;

    public ConfigValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }


    public Object getValue() {
        return value;
    }

    public Object getConfigValue() {
        return ConfigKit.duang().key(getKey()).defaultValue(getValue()).asObject();
    }





}
