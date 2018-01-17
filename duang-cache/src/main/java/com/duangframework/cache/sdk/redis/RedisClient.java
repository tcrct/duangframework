package com.duangframework.cache.sdk.redis;

import com.duangframework.cache.common.AbstractRedisICache;
import com.duangframework.cache.common.AbstractRedisClient;
import com.duangframework.cache.utils.SerializableUtils;
import com.duangframework.core.common.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

import java.io.Serializable;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class RedisClient extends AbstractRedisClient {

    private static RedisClient ourInstance = new RedisClient();


    public static RedisClient getInstance() {
        return ourInstance;
    }

    private RedisClient() {

    }

    public <T extends Serializable> T get(final String key, final Class<T> typeReference){
        return call(new AbstractRedisICache<T>(){
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(key));
                try {
                    String data = new String(bytes, Const.ENCODING_FIELD);
                    if(typeReference.equals(String.class)){
                        return (T)data;
                    } else if(typeReference.equals(Integer.class) || typeReference.equals(int.class)){
                        return (T)new Integer(data);
                    } else if(typeReference.equals(Long.class) || typeReference.equals(long.class)){
                        return (T)new Long(data);
                    } else if(typeReference.equals(Double.class) || typeReference.equals(double.class)){
                        return (T)new Double(data);
                    }
                } catch (Exception e) {
                    throw new JedisException(e.getMessage(), e);
                }
                return SerializableUtils.deserialize(bytes, typeReference);
            }
        });
    }
}
