package com.duangframework.config.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.core.interfaces.IConfig;
import com.duangframework.core.kit.ToolsKit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2018/1/9.
 */
public abstract class AbstractConfig implements IConfig {



    protected Map<String, Object> VALUE_MAP = new HashMap<>();

    protected <T> T getValue(String key) {
        Object value = VALUE_MAP.get(key);
        if(value instanceof JSONObject) {
            JSONObject valueTmp = (JSONObject)value;
            return (T)valueTmp.toJSONString();
        }
        if(value instanceof JSONArray) {
            JSONArray valueTmpArray = (JSONArray)value;
            return (T)valueTmpArray.toArray();
        }
        return (T)VALUE_MAP.get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String tmpValue = getValue(key);
        return ToolsKit.isEmpty(tmpValue) ? "" : tmpValue;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getDoubleValue(key, defaultValue).intValue();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getDoubleValue(key, defaultValue).longValue();
    }

    private Double getDoubleValue(String key,double defaultValue) {
        Double tmpValue = getValue(key);
        return ToolsKit.isEmpty(tmpValue) ? defaultValue : tmpValue;
    }


    @Override
    public double getDouble(String key, double defaultValue) {
        Double tmpValue = getValue(key);
        return ToolsKit.isEmpty(tmpValue) ? 0D : tmpValue;
    }

    @Override
    public String[] getStringArray(String key) {
        JSONArray jsonArray = getValue(key);
        return ToolsKit.isEmpty(jsonArray) ? null : jsonArray.toArray(new String[]{});
    }

    @Override
    public List<String> getList(String key, List<?> defaultValue) {
        String[] array = getStringArray(key);
        if(ToolsKit.isNotEmpty(array)) {
            return Arrays.asList(array);
        }
        return null;
    }

}
