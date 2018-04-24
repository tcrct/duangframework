package com.duangframework.cache.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author Created by laotang
 * @date createed in 2018/4/24.
 */
public class CacheClientExt {

    private String key;
    private Jedis jedis;
    private JedisCluster jedisCluster;
    private CacheDbConnect connect;

    public CacheClientExt(String key, Jedis jedis, CacheDbConnect connect) {
        this.key = key;
        this.jedis = jedis;
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

    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
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
