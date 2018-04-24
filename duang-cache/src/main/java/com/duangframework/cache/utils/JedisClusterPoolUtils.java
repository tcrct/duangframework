/**
 * 
 */
package com.duangframework.cache.utils;

import com.duangframework.cache.common.CacheDbConnect;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * @author laotang
 */
public class JedisClusterPoolUtils {

    private static Logger logger      = LoggerFactory.getLogger(JedisClusterPoolUtils.class);

    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     * 
     */
    private JedisCluster createJedisPool(CacheDbConnect cacheDbConnect) {
        try {
            String[] ipArray = getClusterIps(cacheDbConnect);
            if(ToolsKit.isEmpty(ipArray)) {
                throw new EmptyNullException("ipArray is null");
            }
            // 只给集群里一个实例就可以
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            for (int i = 0; i < ipArray.length; i++) {
                String[] items = ipArray[i].split(":");
                jedisClusterNodes.add(new HostAndPort(items[0], Integer.parseInt(items[1])));
            }
            // 配置信息
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(10000);
            config.setMaxIdle(500);
            config.setMinIdle(100);
            config.setMaxWaitMillis(5000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, config);
            if(null != jedisCluster){
            	logger.warn("Connent Redis Cluster:  "+ ToolsKit.toJsonString(ipArray)+" is Success...");
            	return jedisCluster;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("初始化 redis 出错啦...");
        }
        return null;
    }

    private static String[] getClusterIps(CacheDbConnect cacheDbConnect) {
        return cacheDbConnect.getHost().split(",");
	}


    /**
     * 获取一个 JedisCluster 对象
     * @param  cacheDbConnect
     * @return
     */
    public synchronized JedisCluster getJedisCluster(CacheDbConnect cacheDbConnect) {
        return createJedisPool(cacheDbConnect);
    }

    public void close(JedisCluster jedisCluster) {
        if (jedisCluster != null){
            try {
                jedisCluster.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

}