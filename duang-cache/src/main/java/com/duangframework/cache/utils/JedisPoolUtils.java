package com.duangframework.cache.utils;

import com.duangframework.cache.common.CacheDbConnect;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;


/**
 *
 */
public class JedisPoolUtils {

	private static final Logger logger = LoggerFactory.getLogger(JedisPoolUtils.class);
	private static int timeout = 2000;

    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     * 
     */
    private JedisPool createJedisPool(CacheDbConnect cacheDbConnect) {
		JedisPool pool = null;
        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();

		config.setMaxIdle(100);
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(5000);
		config.setTestWhileIdle(false);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(false);
		config.setNumTestsPerEvictionRun(10);
		config.setMinEvictableIdleTimeMillis(1000);
		config.setSoftMinEvictableIdleTimeMillis(10);
		config.setTimeBetweenEvictionRunsMillis(10);
		config.setLifo(false);

     // 创建连接池       
        try{
			int database = Integer.parseInt(cacheDbConnect.getDataBase());
			String host = cacheDbConnect.getHost();
			String password = cacheDbConnect.getPassWord();
			int port = cacheDbConnect.getPort();
			if(ToolsKit.isEmpty(password)) {
				if(host.contains(":")) {
					String[] hostArray = host.split(":");
					if(ToolsKit.isNotEmpty(hostArray)) {
						try{
							host = hostArray[0];
							port = Integer.parseInt(hostArray[1]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				pool = new JedisPool(config, host, port, timeout);
			} else {
				pool = new JedisPool(config, host, port, timeout, password, database);
			}
			System.out.println("Connent  " + host + ":"+port +" Redis is Success...");
			return pool;
        }catch(Exception e){
        	e.printStackTrace();
        	throw new JedisException(e.getMessage(), e);
        }
    }

    /**
     * 获取一个jedis 对象
     * 
     * @return
     */
    public synchronized JedisPool getJedisPool(CacheDbConnect cacheDbConnect) {
		JedisPool pool = createJedisPool(cacheDbConnect);
		return (null != pool) ? pool : null;
    }

    /**
     * 归还一个连接
     * 
     * @param jedis
     */
    public static void returnResource(JedisPool pool, Jedis jedis) {
    	try {
//    		pool.returnResource(jedis);
            jedis.close();
		} catch (Exception e) {
			throw new JedisException(e.getMessage());
		}
    }
    
    /**
     * 归还一个损坏的连接
     * @param jedis
     */
    public static void returnBrokenResource(JedisPool pool, Jedis jedis) {
    	try {
//    		pool.returnBrokenResource(jedis);
            jedis.close();
		} catch (Exception e) {
			throw new JedisException(e.getMessage());
		}
    }

	public void close(JedisPool pool) {
		if (pool != null){
			pool.close();
		}
	}

}