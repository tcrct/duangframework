package com.duangframework.cache.core;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public interface ICache<E> {

    <T> T call(E cacheAction);
}
