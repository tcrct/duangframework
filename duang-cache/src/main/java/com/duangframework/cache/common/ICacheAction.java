package com.duangframework.cache.common;

import com.duangframework.cache.core.IJedisCache;

/**
 * 缓存的执行方法接口
 * @author laotang
 */
public interface ICacheAction<T> {
	
	T execute(IJedisCache jedis);

//	T execute(JedisCluster jedisCluster);
}
