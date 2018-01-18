package com.duangframework.cache.utils;

import com.duangframework.cache.sdk.redis.RedisClient;
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

    private static JedisPool pool = null;
    
    private static String host;
	private static int port;
	private static int timeout = 2000;
	private static String password;
	private static int database = 0;
	private static JedisPoolUtils _JedisPoolKit;
    
    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     * 
     */
    private static void createJedisPool() {

        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();

		config.setMaxIdle(4000);
		config.setMinIdle(1000);
		config.setMaxTotal(4000);
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
			database = RedisClient.getInstance().getDatabase();
			host = RedisClient.getInstance().getHost();
			password = RedisClient.getInstance().getPassword();
			port = RedisClient.getInstance().getPort();
			pool = new JedisPool(config, host, port, timeout, password, database);
			System.out.println("Connent  " + host + ":"+port +" Redis is Success...");
        }catch(Exception e){
        	e.printStackTrace();
        	throw new JedisException(e.getMessage(), e);
        }
    }
    
    public static boolean isSuccess() {
		return ToolsKit.isNotEmpty(pool);
	}

    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() throws JedisException {
        if (pool == null) {
			createJedisPool();
		}
    }

    /**
     * 获取一个jedis 对象
     * 
     * @return
     */
    public static Jedis getJedis() {
    	if (pool == null){
            poolInit();
    	}
        return pool.getResource();
    }

    /**
     * 归还一个连接
     * 
     * @param jedis
     */
    public static void returnResource(Jedis jedis) {
    	try {
    		pool.returnResource(jedis);	
		} catch (Exception e) {
			throw new JedisException(e.getMessage());
		}
    }
    
    /**
     * 归还一个损坏的连接
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
    	try {
    		pool.returnBrokenResource(jedis);	
		} catch (Exception e) {
			throw new JedisException(e.getMessage());
		}
    }

	public static void close() {
		if (pool != null){
			pool.close();
		}
	}

}