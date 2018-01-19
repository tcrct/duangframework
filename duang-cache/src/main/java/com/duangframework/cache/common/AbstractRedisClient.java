package com.duangframework.cache.common;

import com.duangframework.cache.core.ICache;
import com.duangframework.cache.core.IJedisCache;
import com.duangframework.cache.sdk.redis.RedisEnums;
import com.duangframework.cache.utils.JedisClusterPoolUtils;
import com.duangframework.cache.utils.JedisPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

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
    private static boolean isCluster;

    public static boolean isCluster() {
        return isCluster;
    }

    public void setCluster(boolean cluster) {
        isCluster = cluster;
    }

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
        // 如果host字段里包含有,号的话，则代表有一个以上的地址，认为是一个集群环境
        setCluster(host.contains(","));
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
        if(isCluster()) {
            logger.info("##############AbstractRedisCache");
            Jedis jedis = JedisPoolUtils.getJedis();
            try {
                result = (T) cacheAction.execute((IJedisCache)jedis);
            } catch (Exception e) {
                JedisPoolUtils.returnBrokenResource(jedis);
                logger.warn(e.getMessage(), e);
            } finally {
                JedisPoolUtils.returnResource(jedis);
            }
        } else {
            logger.info("##############AbstractRedisClusterCache");
            IJedisCache jedisCluster = (IJedisCache)JedisClusterPoolUtils.getJedisCluster();
            result = (T) cacheAction.execute(jedisCluster);
        }
        return result;
    }
}
