package com.duangframework.core.kit;

import com.duangframework.core.common.Properties;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 解释duang.properties文件
 * 用作整个系统的配置
 * @author laotang
 * @date 2017/11/16 0016
 */
public class ConfigKit {

    private static Logger logger = LoggerFactory.getLogger(ConfigKit.class);

    private static ConfigKit _configKit;
    private static Lock _configKitLock = new ReentrantLock();
    private static Configuration _configuration;
    private static String _key;
    private static Object _defaultValue;

    public static ConfigKit duang() {
        if(null == _configKit) {
            try {
                _configKitLock.lock();
                _configKit = new ConfigKit();
                _configuration = Properties.init();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _configKitLock.unlock();
            }
        }
        clear();
        return _configKit;
    }

    private static void clear() {
        _key = "";
        _defaultValue = null;
    }

    private ConfigKit() {
    }

    public ConfigKit key(String key) {
        _key = key;
        return this;
    }

    public ConfigKit defaultValue(Object defaultValue) {
        _defaultValue = defaultValue;
        return this;
    }

    public String asString() {
        return _configuration.getString(_key, _defaultValue+"");
    }

    public int asInt() {
        try {
            return _configuration.getInt(_key, Integer.parseInt(_defaultValue + ""));
        } catch (Exception e) {
            return -1;
        }
    }

    public long asLong() {
        try {
            return _configuration.getLong(_key, Long.parseLong(_defaultValue + ""));
        } catch (Exception e) {
            return -1L;
        }
    }

    public double asDouble() {
        try {
            return _configuration.getDouble(_key, Double.parseDouble(_defaultValue + ""));
        } catch (Exception e) {
            return -1d;
        }
    }

    public String[] asArray() {
        try {
            return _configuration.getStringArray(_key);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> asList() {
        try {
            List<Object> objectList =  _configuration.getList(_key, new ArrayList());
            List<String> resultList = new ArrayList<>(objectList.size());
            for(Object object : objectList) {
                resultList.add(object+"");
            }
            return resultList;
        } catch (Exception e) {
            return null;
        }
    }



}
