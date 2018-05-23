package com.duangframework.cache.common;

import redis.clients.jedis.Jedis;

/**
 * 缓存的执行方法接口
 * @author laotang
 */
public interface AliyunJedisAction<T> {
	
	T execute(Jedis jedis);
}
