package com.duangframework.cache.common;

import com.duangframework.cache.utils.JedisPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public abstract class AbstractRedisClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRedisClient.class);
    private static boolean isCluster;
    private static CacheClientExt _clientExt;

    protected static void setCacheClientExt(CacheClientExt clientExt) {
        _clientExt = clientExt;
        isCluster = clientExt.getConnect().getHost().contains(",");
    }

    /**
     * 是否集群对象
     * @return   是集群返回true
     */
    protected static boolean isCluster() {
        return isCluster;
    }

    protected static Jedis getJedis() {
        return _clientExt.getJedisPool().getResource();
    }

    protected static JedisCluster getJedisCluster() {
        return _clientExt .getJedisCluster();
    }

    public <T> T call(JedisAction cacheAction) {
        T result = null;
        if(!isCluster) {
            Jedis jedis = getJedis();
            try {
                result = (T) cacheAction.execute(jedis);
            } catch (Exception e) {
                JedisPoolUtils.returnBrokenResource(_clientExt.getJedisPool(), jedis);
                logger.warn(e.getMessage(), e);
            }
            finally {
                JedisPoolUtils.returnResource(_clientExt.getJedisPool(), jedis);
            }
        } else {
            JedisCluster jedisCluster = getJedisCluster();
            result = (T) cacheAction.execute(jedisCluster);
        }
        return result;
    }



}
