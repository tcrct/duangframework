package com.duangframework.cache.core;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/1/19.
 */
public class MyJedisCache extends Jedis implements IJedisCache {

    @Override
    public Object eval(String script, String key) {
        return null;
    }

    @Override
    public Object evalsha(String script, String key) {
        return null;
    }

    @Override
    public Boolean scriptExists(String sha1, String key) {
        return null;
    }

    @Override
    public List<Boolean> scriptExists(String key, String... sha1) {
        return null;
    }

    @Override
    public String scriptLoad(String script, String key) {
        return null;
    }
}
