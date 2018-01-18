/**
 * 
 */
package com.duangframework.cache.utils;
import com.duangframework.cache.sdk.redis.RedisClusterClient;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author laotang
 */
public class JedisClusterPoolUtils {

    private static Logger logger      = LoggerFactory.getLogger(JedisClusterPoolUtils.class);

    private static JedisCluster jedisCluster = null;
    private static Map<String, String> map         = null;
    private static String _cacheType = "";
    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     * 
     */
    private static void createJedisPool() {
        try {
            String[] ipArray = getClusterIps();
            if(ToolsKit.isEmpty(ipArray)) {
                throw new EmptyNullException("ipArray is null");
            }
            // 只给集群里一个实例就可以
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
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
            jedisCluster = new JedisCluster(jedisClusterNodes, config);
            if(null != jedisCluster){
            	logger.warn("Connent Redis Cluster:  "+ ToolsKit.toJsonString(jedisClusterNodes)+" is Success...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("初始化 redis 出错啦...");
        }
    }

    public static boolean isSuccess() {
        return ToolsKit.isNotEmpty(jedisCluster);
    }
    
    private static String[] getClusterIps() {
        String host = RedisClusterClient.getInstance().getHost();
        return host.split(",");
	}
    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (jedisCluster == null){
        	createJedisPool();
        }
    }

    /**
     * 获取一个jedis 对象
     * 
     * @return
     */
    public static JedisCluster getJedisCluster() {
        if (jedisCluster == null) {
            poolInit();
        }
        return jedisCluster;
    }

    public static void close() {
        if (jedisCluster != null){
            try {
                jedisCluster.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

}