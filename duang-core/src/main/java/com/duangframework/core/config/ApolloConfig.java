package com.duangframework.core.config;

import com.duangframework.config.kit.ApolloConfigKit;
import com.duangframework.core.interfaces.IConfig;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/3/7.
 */
public class ApolloConfig implements IConfig{

    @Override
    public void initValue2Map() {

    }

    @Override
    public String getString(String key, String defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asString();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asInt();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asLong();
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asBoolean();
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asDouble();
    }

    @Override
    public String[] getStringArray(String key) {
        return ApolloConfigKit.duang().key(key).asArray();
    }

    @Override
    public <T> T getList(String key, List<T> defaultValue) {
        return null;
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        return ApolloConfigKit.duang().key(key).defaultValue(defaultValue).asString();
    }

}
