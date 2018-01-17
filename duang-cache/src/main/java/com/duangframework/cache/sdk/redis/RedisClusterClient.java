package com.duangframework.cache.sdk.redis;

import com.duangframework.cache.common.AbstractRedisClient;
import com.duangframework.cache.common.AbstractRedisClusterICache;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class RedisClusterClient extends AbstractRedisClient {

    private static RedisClusterClient ourInstance = new RedisClusterClient();

    public static RedisClusterClient getInstance() {
        return ourInstance;
    }

    private RedisClusterClient() {

    }


    public <T extends Serializable> T get(final String key, final Class<T> typeReference){
        return call(new AbstractRedisClusterICache<T>(){
            @Override
            public T execute(JedisCluster jedisCluster) {
                return null;
            }
        });
    }

}
