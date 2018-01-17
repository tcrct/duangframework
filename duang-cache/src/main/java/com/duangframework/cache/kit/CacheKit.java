package com.duangframework.cache.kit;

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

    public static CacheKit duang() {
        if(null == _cacheKit) {
            try {
                _cacheKitLock.lock();
                _cacheKit = new CacheKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _cacheKitLock.unlock();
            }
        }
        clear();
        return _cacheKit;
    }

    private static void clear() {

    }
}
