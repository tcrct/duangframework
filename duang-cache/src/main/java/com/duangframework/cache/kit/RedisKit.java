package com.duangframework.cache.kit;

import com.duangframework.cache.sdk.redis.AliyunRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by laotang
 * @date createed in 2018/5/23.
 */
public class RedisKit {

    private final static Logger logger = LoggerFactory.getLogger(RedisKit.class);

    private static AliyunRedisClient ourInstance;

    public static AliyunRedisClient duang() {
        try {
            if (null == ourInstance) {
                ourInstance = AliyunRedisClient.duang();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return ourInstance;
    }
}
