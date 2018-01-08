package com.duangframework.core.interfaces;

import java.util.List;

/**
 * Created by laotang on 2018/1/8.
 */
public interface IConfig {

    /**
     * 取String字符串值
     * @param key                   关键字
     * @param defaultValue   默认值
     * @return
     */
    String getString(String key, String defaultValue);

    /**
     * 取int值
     * @param key
     * @param defaultValue
     * @return
     */
    int getInt(String key, int defaultValue);

    /**
     * 取long值
     * @param key
     * @param defaultValue
     * @return
     */
    long getLong(String key, long defaultValue);

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    double getDouble(String key, double defaultValue);

    /**
     *  取字符串数据
     * @param key
     * @return
     */
    String[] getStringArray(String key);

    /**
     *  取List集合
     * @param key
     * @param defaultValue
     * @return
     */
    List<String> getList(String key, List<?> defaultValue);
}
