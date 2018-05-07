package com.duangframework.cache.common;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * @author Created by laotang
 * @date createed in 2018/4/24.
 */
public class CacheClientExt {

    private String key;
    private JedisPool jedisPool;
    private JedisCluster jedisCluster;
    private CacheDbConnect connect;

    public CacheClientExt(String key, JedisPool jedisPool, CacheDbConnect connect) {
        this.key = key;
        this.jedisPool = jedisPool;
        this.connect = connect;
    }

    public CacheClientExt(String key, JedisCluster jedisCluster, CacheDbConnect connect) {
        this.key = key;
        this.jedisCluster = jedisCluster;
        this.connect = connect;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public CacheDbConnect getConnect() {
        return connect;
    }

    public void setConnect(CacheDbConnect connect) {
        this.connect = connect;
    }
}
