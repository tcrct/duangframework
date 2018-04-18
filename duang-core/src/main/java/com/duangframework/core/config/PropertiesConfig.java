package com.duangframework.core.config;

import com.duangframework.core.interfaces.IConfig;
import com.duangframework.core.kit.PropertiesKit;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/3/7.
 */
public class PropertiesConfig implements IConfig {

    @Override
    public void initValue2Map() {

    }

    @Override
    public String getString(String key, String defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asString();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asInt();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asLong();
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asBoolean();
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asDouble();
    }

    @Override
    public String[] getStringArray(String key) {
        return PropertiesKit.duang().key(key).asArray();
    }

    @Override
    public <T> T getList(String key, List<T> defaultValue) {
        return (T) PropertiesKit.duang().key(key).asList();
    }

    @Override
      public Object getObject(String key, Object defaultValue) {
        return PropertiesKit.duang().key(key).defaultValue(defaultValue).asObject();
    }
}
