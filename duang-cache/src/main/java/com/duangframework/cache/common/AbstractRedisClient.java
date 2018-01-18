package com.duangframework.cache.common;

import com.duangframework.cache.core.ICache;
import com.duangframework.cache.sdk.redis.RedisEnums;
import com.duangframework.cache.utils.JedisPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public abstract class AbstractRedisClient  implements ICache<ICacheAction> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRedisClient.class);

    protected RedisEnums redisEnums;
    protected String host;
    protected String password;
    protected int port;
    protected int database = 0;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public AbstractRedisClient() {
    }

    public RedisEnums getRedisEnums() {
        return redisEnums;
    }

    public void setRedisEnums(RedisEnums redisEnums) {
        this.redisEnums = redisEnums;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public <T> T call(ICacheAction cacheAction) {
        T result = null;
        if(cacheAction instanceof AbstractRedisCache) {
            logger.info("##############AbstractRedisCache");
            Jedis jedis = JedisPoolUtils.getJedis();
            try {
                result = (T) cacheAction.execute(jedis);
            } catch (Exception e) {
                JedisPoolUtils.returnBrokenResource(jedis);
                logger.warn(e.getMessage(), e);
            } finally {
                JedisPoolUtils.returnResource(jedis);
            }
        } else if(cacheAction instanceof AbstractRedisClusterCache) {
            logger.info("##############AbstractRedisClusterCache");
            JedisCluster jedisCluster = null;
            result = (T) cacheAction.execute(jedisCluster);
        }
        return result;
    }
}
