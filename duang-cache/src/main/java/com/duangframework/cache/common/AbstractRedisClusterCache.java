package com.duangframework.cache.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public abstract class AbstractRedisClusterCache<T> implements ICacheAction<T> {

    @Override
    public T execute(Jedis jedis) {
        return null;
    }

    @Override
    public abstract T execute(JedisCluster jedisCluster);
}
