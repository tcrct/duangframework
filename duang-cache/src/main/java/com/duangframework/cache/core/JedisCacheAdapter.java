package com.duangframework.cache.core;

/**
 * @author Created by laotang
 * @date createed in 2018/1/19.
 */
public abstract class JedisCacheAdapter implements IJedisCache {

    private IJedisCache jedis;

    public JedisCacheAdapter(IJedisCache jedis) {
        this.jedis = jedis;
    }


    public IJedisCache getJedis() {
        return jedis;
    }

}
