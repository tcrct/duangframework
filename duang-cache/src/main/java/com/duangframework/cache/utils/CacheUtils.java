package com.duangframework.cache.utils;

import com.duangframework.cache.common.CacheClientExt;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by laotang
 * @date createed in 2018/4/24.
 */
public class CacheUtils {

    private static CacheClientExt clientExt;
    private static ConcurrentHashMap<String, CacheClientExt> CACHE_CLIENT_EXT_MAP = new ConcurrentHashMap();

    public static void setDefaultClientExt(CacheClientExt clientExt) {
        CacheUtils.clientExt = clientExt;
    }

    public static CacheClientExt getDefaultClientExt() {
        return CacheUtils.clientExt;
    }

    public static void setCacheClientExt(String key, CacheClientExt clientExt) {
        CACHE_CLIENT_EXT_MAP.put(key, clientExt);
    }

    public static CacheClientExt getCacheClientExt(String key) {
        return CACHE_CLIENT_EXT_MAP.get(key);
    }

    public static void close() throws Exception {
        for(Iterator<Map.Entry<String, CacheClientExt>>  iterator = CACHE_CLIENT_EXT_MAP.entrySet ().iterator(); iterator.hasNext();) {
            Map.Entry<String, CacheClientExt> entry = iterator.next();
            JedisPool jedisPool = entry.getValue().getJedisPool();
            if(null != jedisPool) {
                jedisPool.close();
            }
            JedisCluster jedisCluster = entry.getValue().getJedisCluster();
            if(null != jedisCluster) {
                jedisCluster.close();
            }
        }
    }
}
