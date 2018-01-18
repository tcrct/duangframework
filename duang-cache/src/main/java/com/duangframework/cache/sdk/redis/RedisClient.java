package com.duangframework.cache.sdk.redis;

import com.alibaba.fastjson.TypeReference;
import com.duangframework.cache.common.AbstractRedisCache;
import com.duangframework.cache.common.AbstractRedisClient;
import com.duangframework.cache.utils.SerializableUtils;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class RedisClient extends AbstractRedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClusterClient.class);
    private static RedisClient ourInstance;

    public static RedisClient getInstance() {
        try {
            if (null == ourInstance) {
                ourInstance = new RedisClient();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return ourInstance;
    }

    private RedisClient() {

    }

    @Override
    public <T> T get(final String key, final Class<T> typeReference){
        return call(new AbstractRedisCache<T>(){
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
                return (T)SerializableUtils.deserialize(bytes, typeReference);
            }
        });
    }

    /**
     *  取值
     * @param key        关键字
     * @param typeReference		泛型
     * @return
     */
    @Override
    public <T> T get(final String key, final TypeReference<T> typeReference){
        return call(new AbstractRedisCache<T>(){
            @Override
            public T execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(key));
                if(ToolsKit.isNotEmpty(bytes)){
                    return (T)SerializableUtils.deserialize(bytes, typeReference);
                }
                return null;
            }
        });
    }

    /**
     *  取数据值
     * @param key                       参数值
     * @param clazz                     要转换的类
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> getArray(final String key, final Class<T> clazz){
        return call(new AbstractRedisCache<List<T>>(){
            @Override
            public List<T> execute(Jedis jedis) {
                byte[] bytes = jedis.get(SafeEncoder.encode(key));
                if(ToolsKit.isNotEmpty(bytes)){
                    try {
                        return (List<T>)SerializableUtils.deserializeArray(bytes, clazz);
                    } catch (Exception e) {
                        throw new JedisException(e.getMessage(), e);
                    }
                }
                return null;
            }
        });
    }

    /**
     * 按key-value方式将值保存到redis
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean set(final String key, final Object value){
        if(null == value){
            logger.warn("RedisClient.set value is null, return false...");
            return false;
        }
        return call(new AbstractRedisCache<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                String result = "";
                if(value instanceof String){
                    result = jedis.set(key, (String) value);
                }else{
                    result = jedis.set(SafeEncoder.encode(key), SerializableUtils.serialize(value));
                }
                return "OK".equalsIgnoreCase(result);
            }
        });
    }

    /**
     * 按key-value方式将值保存到redis, 缓存时间为seconds, 过期后会自动将该key指向的value删除
     * @param key			关键字
     * @param value			值
     * @param seconds		缓存时间，秒作单位
     * @return
     */
    @Override
    public boolean set(final String key, final Object value, final int seconds){
        return call(new AbstractRedisCache<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                String result = "";
                if(value instanceof String){
                    result = jedis.setex(key, seconds,  (String) value);
                }else{
                    result = jedis.setex(key, seconds, ToolsKit.toJsonString(value));
                }
                return "OK".equalsIgnoreCase(result);
            }
        });
    }

    /**
     * 根据key删除指定的内容
     * @param keys
     * @return
     */
    @Override
    public long del(final String... keys){
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(keys);
            }
        });
    }

    /**
     * 将内容添加到list里的第一位
     * @param key		关键字
     * @param value		内容
     * @return
     */
    @Override
    public long lpush(final String key, final Object value) {
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.lpush(key, (String)value);
                }else{
                    return jedis.lpush(SafeEncoder.encode(key), SerializableUtils.serialize(value));
                }
            }
        });
    }

    /**
     * 将内容添加到list里的最后一位
     * @param key		关键字
     * @param value		内容
     * @return
     */
    public long rpush(final String key, final Object value) {
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.rpush(key, (String)value);
                }else{
                    return jedis.rpush(SafeEncoder.encode(key), SerializableUtils.serialize(value));
                }
            }
        });
    }

    /**
     * 根据key取出list集合
     * @param key			关键字
     * @param start			开始位置(0表示第一个元素)
     * @param end			结束位置(-1表示最后一个元素)
     * @return
     */
    public List<String> lrange(final String key, final int start, final int end) {
        return call(new AbstractRedisCache<List<String>>(){
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.lrange(key, start, end);
            }
        });
    }

    /**
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
        count 的值可以是以下几种：
             count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
             count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
             count = 0 : 移除表中所有与 value 相等的值。
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long lrem(final String key, final int count, final Object value){
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if(value instanceof String){
                    return jedis.lrem(key, count, (String)value);
                }else{
                    return  jedis.lrem(SafeEncoder.encode(key), count, SerializableUtils.serialize(value));
                }
            }
        });
    }

    /**
     * 向名称为key的hash中添加元素(map)
     * @param key
     * @param values		map<String,?>
     * @return
     */
    public boolean hmset(final String key, final Map<String, String> values) {
        return call(new AbstractRedisCache<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                String isok = jedis.hmset(key, values);
                return "OK".equalsIgnoreCase(isok);
            }
        });
    }

    /**
     * 返回名称为key在hash中fields对应的value
     * @param key		关键字
     * @param fields	hash中的field
     * @return
     */
    public List<String> hmget(final String key, final String... fields) {
        return call(new AbstractRedisCache<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    /**
     * 返回名称为key的hash中fields对应的value
     * @param key		关键字
     * @param fields	hash中的field
     * @return
     */
    public Map<String,String> hmgetToMap(final String key, final String... fields) {
        return call(new AbstractRedisCache<Map<String,String>>() {
            @Override
            public Map<String, String> execute(Jedis jedis) {
                List<String> byteList = jedis.hmget(key, fields);
                Map<String,String> map = new HashMap<>();
                int size  = byteList.size();
                for (int i = 0; i < size; i ++) {
                    if(ToolsKit.isNotEmpty(byteList.get(i))){
                        map.put(fields[i], byteList.get(i));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 删除指定hash里的field
     * @param key
     * @param fields
     * @return
     */
    public long hdel(final String key, final String... fields) {
        return call(new AbstractRedisCache<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hdel(key, fields);
            }
        });
    }

    /**
     * 取出指定hash里的所有field
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key) {
        return call(new AbstractRedisCache<Set<String>>() {
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    /**
     * 判断hashmap里面是否存在field的key
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(final String key, final String field) {
        return call(new AbstractRedisCache<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                if (null != field) {
                    return jedis.hexists(key,  field);
                }
                return false;
            }
        });
    }

    /**
     * 返回名称为key的hash中fields对应的value
     * @param key       关键字
     * @param field    hash中的field
     * @return
     */
    public String hget(final String key, final String field) {
        return call(new AbstractRedisCache<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(key,  field);
            }
        });
    }

    /**
     * key返回哈希表key中，所有的域和值
     * @param key
     * @return
     */
    public Map<String,String> hgetAll(final String key) {
        return call(new AbstractRedisCache<Map<String,String>>() {
            @Override
            public Map<String,String> execute(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    /**
     * 向有序set里添加元素
     * @param key		set的key
     * @param value		对应的value
     * @return
     */
    public boolean sadd(final String key, final Object value) {
        return call(new AbstractRedisCache<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                long isok = 0;
                if(value instanceof String){
                    isok = jedis.sadd(key, (String)value);
                }else{
                    isok = jedis.sadd(SafeEncoder.encode(key), SerializableUtils.serialize(value));
                }
                return isok == 1 ? true : false;
            }
        });
    }

    /**
     * 返回名称为key的set的基数
     * @param key		set的key
     * @return
     */
    public Long scard(final String key) {
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    /**
     * 测试member是否是名称为key的set的元素
     * @param key		Set集合的key
     * @param value		值
     * @return
     */
    public boolean sismember(final String key, final Object value) {
        return call(new AbstractRedisCache<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.sismember(key, (String)value);
            }
        });
    }

    /**
     * 慎用，会导致redis等待结果返回，若是集群模式则直接返回null
     * @param pattern		正则表达式
     * @return
     */
    public Set<String> keys(final String pattern){
        return call(new AbstractRedisCache<Set<String>>(){
            @Override
            public Set<String> execute(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    /**
     * 根据标识取出redis里的集合size
     * @param type		标识("list", "hash", "set")
     * @param key		关键字
     * @return
     */
    public long size(final String type, final String key) {
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                if("list".equalsIgnoreCase(type)){
                    return jedis.llen(key);
                }else if("hash".equalsIgnoreCase(type)){
                    return jedis.hlen(key);
                }else if("set".equalsIgnoreCase(type)){
                    return jedis.scard(key);
                }
                return 0L;
            }
        });
    }

    /**
     * 根据key判断值类型
     * @param key		关键字
     * @return		类型名称
     */
    public String type(final String key) {
        return call(new AbstractRedisCache<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.type(key);
            }
        });
    }

    /**
     * 判断KEY是否存在
     * @param key		关键字
     * @return			存在返回true
     */
    public boolean exists(final String key) {
        return call(new AbstractRedisCache<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    /**
     * 根据key设置过期时间
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final Integer seconds) {
        return call(new AbstractRedisCache<Long>(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    /**
     * 保存ZSet<String>
     * @param key
     * @param sort
     * @param value
     * @return
     */
    public Boolean zadd(final String key ,final double sort ,final String value){
        return call(new AbstractRedisCache<Boolean>(){
            @Override
            public Boolean execute(Jedis jedis) {
                try {
                    long count = jedis.zadd(key, sort, value);
                    return count > 0 ? true : false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    /**
     * 删除ZSet元素
     * @param key
     * @param value
     * @return
     */
    public Long zrem(final String key ,final String value){
        return call(new AbstractRedisCache<Long >(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrem(key, value);
            }
        });
    }

    /**
     * 由小到大获取member成员在该key的位置
     * @param key
     * @param member
     * @return
     */
    public Long zrank(final String key, final String member){
        return call(new AbstractRedisCache<Long >(){
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrank(key, member);
            }
        });
    }
}
