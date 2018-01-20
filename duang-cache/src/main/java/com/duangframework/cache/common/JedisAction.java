package com.duangframework.cache.common;

/**
 * 缓存的执行方法接口
 * @author laotang
 */
public interface JedisAction<T> {
	
	T execute(Object jedisObj);
}
