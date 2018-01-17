package com.duangframework.cache.plugin;

import com.duangframework.cache.sdk.redis.RedisClient;
import com.duangframework.cache.sdk.redis.RedisClusterClient;
import com.duangframework.cache.utils.JedisClusterUtils;
import com.duangframework.cache.utils.JedisPoolUtils;
import com.duangframework.core.interfaces.IPlugin;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class CachePlugin implements IPlugin {

    private boolean isClusterRedis = false;

    public CachePlugin(String endpoint, String pwd, int database) {
        String [] arrayItem = endpoint.split(",");
        if(arrayItem.length > 1) {
            RedisClusterClient.getInstance().setHost(endpoint);
            RedisClusterClient.getInstance().setPort(0);
            RedisClusterClient.getInstance().setDatabase(database);
            RedisClusterClient.getInstance().setPassword(pwd);
            isClusterRedis = true;
        } else {
            String [] endpointArray = endpoint.split(":");
            RedisClient.getInstance().setHost(endpointArray[0]);
            RedisClient.getInstance().setPort(Integer.parseInt(endpointArray[1]));
            RedisClient.getInstance().setDatabase(database);
            RedisClient.getInstance().setPassword(pwd);
        }
    }

    public CachePlugin(String host, int port, String pwd, int database) {
        RedisClient.getInstance().setHost(host);
        RedisClient.getInstance().setPort(port);
        RedisClient.getInstance().setDatabase(database);
        RedisClient.getInstance().setPassword(pwd);
    }

//    public CachePlugin(RedisEnums enums) {
//        boolean isClusterRedis = RedisEnums.ECS_CLUSTER.getCode().equalsIgnoreCase(enums.getCode());
//        //如果集群
//        if(isClusterRedis) {
//        } else {
//            RedisClient.getInstance().setHost(enums.getIp());
//            RedisClient.getInstance().setPort(enums.getPort());
//            RedisClient.getInstance().setDatabase(enums.getDataBase());
//            RedisClient.getInstance().setPassword(enums.getPwd());
//        }
//    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start() throws Exception {
        if(!isClusterRedis) {
            JedisPoolUtils.getJedis();
        } else {
            JedisClusterUtils.getJedisCluster();
        }
    }

    @Override
    public void stop() throws Exception {
        if(!isClusterRedis) {
            JedisPoolUtils.close();
        } else {
            JedisClusterUtils.close();
        }
    }
}
