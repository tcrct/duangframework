package com.duangframework.config.kit;


import com.duangframework.config.apollo.api.SimpleApolloConfig;
import com.duangframework.config.client.ConfigClient;
import com.duangframework.config.utils.ConfigUtils;
import com.duangframework.config.utils.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 用作整个系统的配置
 * @author laotang
 * @date 2017/11/16 0016
 */
public class ConfigKit {

    private static Logger logger = LoggerFactory.getLogger(ConfigKit.class);

    private static SimpleApolloConfig apolloConfig;
    private String _key;
    private Object _defaultValue;

    public static ConfigKit duang() {
        return new ConfigKit();
    }


    private ConfigKit() {
        apolloConfig = ConfigClient.getApolloConfig();
        if(ConfigUtils.isEmpty(apolloConfig)) {
            throw new NullPointerException("请先启动ConfigPlugin插件");
        }
    }

    public ConfigKit key(String key) {
        _key = key;
        return this;
    }


    public ConfigKit defaultValue(Object defaultValue) {
        _defaultValue = defaultValue;
        return this;
    }

    private Object getConfigValue(Class type) {
        String value = apolloConfig.getConfig(_key);
        if(DataType.isString(type)) {
            return  ConfigUtils.isEmpty(value) ? _defaultValue : value;
        }
        else if(DataType.isInteger(type) || DataType.isIntegerObject(type)) {
            return ConfigUtils.isEmpty(value)? Integer.parseInt(_defaultValue+"") : Integer.parseInt(value);
        }
        else if(DataType.isFloat(type) || DataType.isFloatObject(type)) {
            return ConfigUtils.isEmpty(value)? Float.parseFloat(_defaultValue+"") : Float.parseFloat(value);
        }
        else if(DataType.isDouble(type) || DataType.isDoubleObject(type)) {
            return ConfigUtils.isEmpty(value)? Double.parseDouble(_defaultValue+"") : Double.parseDouble(value);
        }
        else if(DataType.isDate(type)) {
            return ConfigUtils.isEmpty(value)? ConfigUtils.parseDate(_defaultValue+"", ConfigUtils.DEFAULT_DATE_FORM) :
                    ConfigUtils.parseDate(value, "");
        } else {
            return value;
        }
    }

    public String asString() {
        try {
            return (String)getConfigValue(String.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "";
        }
    }

    public Integer asInt() {
        try {
            return (Integer) getConfigValue(int.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return -1;
        }
    }

    public Long asLong() {
        try {
            return (Long) getConfigValue(Long.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return -1L;
        }
    }

    public Double asDouble() {
        try {
            return (Double) getConfigValue(Double.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return -1d;
        }
    }

    public Date asDate() {
        try {
            return (Date) getConfigValue(Date.class);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }
}
