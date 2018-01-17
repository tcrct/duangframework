package com.duangframework.cache.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * 缓存的执行方法接口
 * @author laotang
 */
public interface ICacheAction<T> {
	
	T execute(Jedis jedis);

	T execute(JedisCluster jedisCluster);
}
