package com.duangframework.cache.core;

import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public interface ICache<E> {

    <T> T call(E cacheAction);

    <T> T get(String key, Class<T> clazz);

    <T> T get(String key, TypeReference<T> type);

    <T> List<T> getArray(String key, Class<T> typeReference);

    boolean set(String key, Object value);

    boolean set(String key, Object value, int seconds);

    long del(final String... keys);

    long lpush(final String key, final Object value);

}
