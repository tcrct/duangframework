package com.duangframework.cache.kit;

import com.duangframework.cache.common.CacheDbConnect;
import com.duangframework.cache.utils.JedisClusterPoolUtils;
import com.duangframework.cache.utils.JedisPoolUtils;
import com.duangframework.core.interfaces.IConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class CacheClientKit {
    private static Logger logger = LoggerFactory.getLogger(CacheClientKit.class);

    private static CacheClientKit _cacheClientKit;
    private static CacheDbConnect _cacheDbConnect;
    private static String _cacheKey;

    public static CacheClientKit duang() {
        return new CacheClientKit();
    }

    public CacheClientKit connect(IConnect connect) {
        _cacheDbConnect = (CacheDbConnect) connect;
        return this;
    }

    public JedisPool getJedisPool() {
        return new JedisPoolUtils().getJedisPool(_cacheDbConnect);
    }

    public JedisCluster getClusterJedis() {
        return new JedisClusterPoolUtils().getJedisCluster(_cacheDbConnect);
    }


}
