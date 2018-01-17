package com.duangframework.cache.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public abstract class AbstractRedisICache<T> implements ICacheAction<T> {

    @Override
    public abstract T execute(Jedis jedis);

    @Override
    public T execute(JedisCluster jedisCluster) {
        return null;
    }
}
