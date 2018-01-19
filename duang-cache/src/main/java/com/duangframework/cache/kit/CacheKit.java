package com.duangframework.cache.kit;

import com.duangframework.cache.sdk.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class CacheKit {
    private static Logger logger = LoggerFactory.getLogger(CacheKit.class);

    private static CacheKit _cacheKit;
    private static Lock _cacheKitLock = new ReentrantLock();
    private static String _cacheKey;
    private static RedisClient _iCache;

    public static RedisClient duang() {
        if(null == _iCache) {
            try {
                _cacheKitLock.lock();
//                if( JedisClusterPoolUtils.isSuccess() ) {
//                    _iCache = RedisClusterClient.getInstance();
//                } else {
//                    _iCache = RedisClient.getInstance();
//                }
                _iCache = RedisClient.getInstance();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _cacheKitLock.unlock();
            }
        }
        return _iCache;
    }

}
