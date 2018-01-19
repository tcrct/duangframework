package com.duangframework.cache.sdk.redis;

import com.alibaba.fastjson.TypeReference;
import com.duangframework.cache.common.AbstractRedisClient;
import com.duangframework.cache.common.ICacheAction;
import com.duangframework.cache.core.IJedisCache;
import com.duangframework.cache.utils.JedisClusterPoolUtils;
import com.duangframework.cache.utils.JedisPoolUtils;
import com.duangframework.cache.utils.SerializableUtils;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.io.Serializable;
import java.util.*;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class RedisClient extends AbstractRedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);
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
        return call(new ICacheAction<T>(){
            @Override
            public T execute(IJedisCache jedis) {
                byte[] bytes = null;
                try {
                    bytes = jedis.get(key).getBytes(Protocol.CHARSET);
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
        return call(new ICacheAction<T>(){
            @Override
            public T execute(IJedisCache jedis) {
                try {
                    byte[] bytes = jedis.get(key).getBytes(Protocol.CHARSET);
                    if (ToolsKit.isNotEmpty(bytes)) {
                        return (T) SerializableUtils.deserialize(bytes, typeReference);
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
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
        return call(new ICacheAction<List<T>>(){
            @Override
            public List<T> execute(IJedisCache jedis) {
                String byteString = jedis.get(key);
                if(ToolsKit.isNotEmpty(byteString)){
                    try {
                        byte[] bytes = byteString.getBytes(Protocol.CHARSET);
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
        return call(new ICacheAction<Boolean>(){
            @Override
            public Boolean execute(IJedisCache jedis) {
                String result = "";
                if(value instanceof String){
                    result = jedis.set(key, (String) value);
                }else{
                    result = jedis.set(key, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<Boolean>(){
            @Override
            public Boolean execute(IJedisCache jedis) {
                String result = "";
                if(value instanceof String){
                    result = jedis.setex(key, seconds,  (String) value);
                }else{
                    result = jedis.setex(key, seconds, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                if(value instanceof String){
                    return jedis.lpush(key, (String)value);
                }else{
                    return jedis.lpush(key, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                if(value instanceof String){
                    return jedis.rpush(key, (String)value);
                }else{
                    return jedis.rpush(key, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<List<String>>(){
            @Override
            public List<String> execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                if(value instanceof String){
                    return jedis.lrem(key, count, (String)value);
                }else{
                    return  jedis.lrem(key, count, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<Boolean>() {
            @Override
            public Boolean execute(IJedisCache jedis) {
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
        return call(new ICacheAction<List<String>>() {
            @Override
            public List<String> execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Map<String,String>>() {
            @Override
            public Map<String, String> execute(IJedisCache jedis) {
                List<String> byteList = jedis.hmget(key, fields);
                Map<String,String> map = new HashMap<>(byteList.size());
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
        return call(new ICacheAction<Long>() {
            @Override
            public Long execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Set<String>>() {
            @Override
            public Set<String> execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Boolean>() {
            @Override
            public Boolean execute(IJedisCache jedis) {
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
        return call(new ICacheAction<String>() {
            @Override
            public String execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Map<String,String>>() {
            @Override
            public Map<String,String> execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Boolean>(){
            @Override
            public Boolean execute(IJedisCache jedis) {
                long isok = 0;
                if(value instanceof String){
                    isok = jedis.sadd(key, (String)value);
                }else{
                    isok = jedis.sadd(key, SerializableUtils.serialize(value));
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Boolean>(){
            @Override
            public Boolean execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Set<String>>(){
            @Override
            public Set<String> execute(IJedisCache jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    /**
     * 根据标识取出redis里的集合size
     * @param type		标识("List.class", "Map.class", "Set.class")
     * @param key		关键字
     * @return
     */
    public long size(final Class<?> type, final String key) {
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                if(List.class.equals(type)){
                    return jedis.llen(key);
                }else if(Map.class.equals(type)){
                    return jedis.hlen(key);
                }else if(Set.class.equals(type)){
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
        return call(new ICacheAction<String>() {
            @Override
            public String execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Boolean>() {
            @Override
            public Boolean execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Boolean>(){
            @Override
            public Boolean execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Long >(){
            @Override
            public Long execute(IJedisCache jedis) {
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
        return call(new ICacheAction<Long >(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.zrank(key, member);
            }
        });
    }

    /**
     * 由大到小获取member成员在该key的位置
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(final String key,final String member){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.zrevrank(key, member);
            }
        });
    }

    /**
     * 升序获取zset元素
     * @param key
     * @return
     */
    public List<String> zrevrank(final String key){
        return call(new ICacheAction<List<String> >(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                return new ArrayList<String>(jedis.zrange(key, 0, -1));
            }
        });
    }

    /**
     * 升序获取zset元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> zrevrank(final String key,final int start, final int end){
        return call(new ICacheAction<List<String> >(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                int e = end;
                if(e > 0){e--;}
                return new ArrayList<String>( jedis.zrange(key, start, e) );
            }
        });
    }

    /**
     * 降序获取zset元素
     * @param key
     * @return
     */
    public List<String> zrevrange(final String key){
        return call(new ICacheAction<List<String> >(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                return new ArrayList<String>( jedis.zrevrange(key, 0, -1) );
            }
        });
    }

    /**
     * 降序获取zset元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> zrevrange(final String key,final int start, final int end){
        return call(new ICacheAction<List<String> >(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                int e = end;
                if(e > 0){e--;}
                return new ArrayList<String>( jedis.zrevrange(key,start, e) );
            }
        });
    }

    /**
     * 取范围值
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> zrange(final String key,final int start, final int end){
        return call(new ICacheAction<List<String> >(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                return new ArrayList<String>( jedis.zrange(key, start, end) );
            }
        });
    }


    /**
     * 根据区间段获取集合内排名成员--倒序
     * @param key   分组key
     * @param start 开始位
     * @param end   结束位  当为-1时，为取所有值
     * @return
     */
    public Set<Tuple> zrevrangeWithScores(final String key, final int start, final int end) {
        return call(new ICacheAction<Set<Tuple>>() {
            @Override
            public Set<Tuple> execute(IJedisCache jedis) {
                return jedis.zrevrangeWithScores(key, start, end);
            }
        });
    }

    /**
     * 根据key获取list长度
     * @param key
     * @return
     */
    public Long llen(final String key){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.llen(key);
            }
        });
    }

    /**
     * 根据key删除并返回list尾元素
     * @param key
     * @returnrpop
     */
    public String rpop(final String key){
        return call(new ICacheAction<String>(){
            @Override
            public String  execute(IJedisCache jedis) {
                return jedis.rpop(key);
            }
        });
    }

    /**
     * 将名称为key的hash中field的value增加integer
     * @param key
     * @param field
     * @param integer
     * @return
     */
    public Long hincrby(final String key,final String field,final Integer integer){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.hincrBy(key, field, integer);
            }
        });
    }

    /**
     * 向名称为key的hash中添加元素field<—>value
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(final String key,final String field, final Object value){
        return call(new ICacheAction<Long >(){
            @Override
            public Long  execute(IJedisCache jedis) {
                if(value instanceof String){
                    return jedis.hset(key, field, (String)value);
                } else {
                    return jedis.hset(key, field, SerializableUtils.serialize(value));
                }
            }
        });
    }


    /**
     * 返回名称为key的zset中score>=min且score<=max的所有元素
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zrangebyscore(final String key, final double min, final double max) {
        return call(new ICacheAction<Set<String>>() {
            @Override
            public Set<String> execute(IJedisCache jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }


    /**
     * 删除名称为key的zset中score>=min且score<=max的所有元素
     */
    public Long zremrangebyscore(final String key, final double min, final double max) {
        return call(new ICacheAction<Long>() {
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.zremrangeByScore(key, min, max);
            }
        });
    }

    /**
     * 删除名称为KEY的zeset中rank>=min且rank<=max的所有元素
     * @param key
     * @param start
     * @param max
     * @return
     */
    public Long zremrangebyrank(final String key, final int start,final int max){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.zremrangeByRank(key, start, max);
            }
        });
    }


    /**
     * 为某个key自增1
     * @param key
     * @return
     */
    public Long incr(final String key){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.incr(key);
            }
        });
    }

    /**
     * 为某个key自减1
     * @param key
     * @return
     */
    public Long decr(final String key){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.decr(key);
            }
        });
    }

    /**
     * 获取set的基数
     * @param key
     * @return
     */
    public Long zcard(final String key){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.zcard(key);
            }
        });
    }

    /**
     * 返回key的有效时间
     * @param key
     * @return
     */
    public Long ttl(final String key){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.ttl(key);
            }
        });
    }

    /**
     * 删除set里面和member相同的元素
     * @param key
     * @param member
     * @return
     */
    public Long srem(final String key,final String member){
        return call(new ICacheAction<Long>(){
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.srem(key, member);
            }
        });
    }

    /**
     * 获取set对象
     * @param key
     * @return
     */
    public Set<String> smembers(final String key){
        return call(new ICacheAction<Set<String>>(){
            @Override
            public Set<String> execute(IJedisCache jedis) {
                return jedis.smembers(key);
            }
        });
    }

    /**
     * 获取zset里面元素的soce
     * @param key
     * @return
     */
    public Double zscore(final String key,final String member){
        return call(new ICacheAction<Double>(){
            @Override
            public Double execute(IJedisCache jedis) {
                return jedis.zscore(key, member);
            }
        });
    }

    /**
     * 返回 key 指定的哈希集中所有字段的值
     * @param key
     * @return
     */
    public List<String> hvals(final String key){
        return call(new ICacheAction<List<String>>(){
            @Override
            public List<String> execute(IJedisCache jedis) {
                return jedis.hvals(key);
            }
        });
    }

    @SuppressWarnings("unused")
    private <T extends Serializable> T batctGet(final Set<String> keys) {
        return call(new ICacheAction<T>(){
            @Override
            @SuppressWarnings("unchecked")
            public T execute(IJedisCache jedis){
                Map<String,String> result = new HashMap<>(keys.size());
                for(String key : keys){
                    result.put(key, jedis.get(key));
                }
                return (T)result;
            }
        });
    }

    /**
     * 添加地理位置
     * @param key					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param member		集合成员值
     * @return
     */
    public long geoadd(final String key, final double longitude, final double latitude, final String member) {
        return call(new ICacheAction<Long>() {
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.geoadd(key, longitude, latitude, member);
            }
        });
    }

    /**
     * 添加地理位置
     * @param key		地理位置集合KEY
     * @param memberCoordinateMap		成员集合值
     * @return
     */
    public long geoadd(final String key, final Map<String, GeoCoordinate> memberCoordinateMap) {
        return call(new ICacheAction<Long>() {
            @Override
            public Long execute(IJedisCache jedis) {
                return jedis.geoadd(key, memberCoordinateMap);
            }
        });
    }

    /**
     * 根据名称获取地理位置信息
     * @param key					地理位置集合KEY
     * @param members		成员值
     * @return
     */
    public List<GeoCoordinate> geopos(final String key, final String... members) {
        return call(new ICacheAction<List<GeoCoordinate>>() {
            @Override
            public List<GeoCoordinate> execute(IJedisCache jedis) {
                return jedis.geopos(key, members);
            }
        });
    }

    /**
     * 计算两个位置之间的距离
     * @param key						地理位置集合KEY
     * @param member1			成员值
     * @param member2			成员值
     * @parma unit						单位(M/KM)
     * @return
     */
    public Double geodist(final String key, final String member1, final String member2, final GeoUnit unit) {
        return call(new ICacheAction<Double>() {
            @Override
            public Double execute(IJedisCache jedis) {
                return jedis.geodist(key, member1, member2, unit);
            }
        });
    }

    /**
     * 获取指定范围内的位置信息
     * @param key					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param radius				半径范围
     * @param unit					单位(M/KM)
     * @return
     */
    public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius, final GeoUnit unit) {
        return call(new ICacheAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(IJedisCache jedis) {
                return jedis.georadius(key, longitude, latitude, radius, unit);
            }
        });
    }

    /**
     * 获取指定范围内的位置信息
     * @param key					地理位置集合KEY
     * @param longitude		经度
     * @param latitude			纬度
     * @param radius				半径范围
     * @param unit					单位(M/KM)
     * @param param			查询条件参数
     * @return
     */
    public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius, final GeoUnit unit, final GeoRadiusParam param) {
        return call(new ICacheAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(IJedisCache jedis) {
                return jedis.georadius(key, longitude, latitude, radius, unit, param);
            }
        });
    }

    /**
     * 获取存储集合范围内的位置信息
     * @param key				地理位置集合KEY
     * @param member		成员名称
     * @param radius			半径范围
     * @param unit				单位(M/KM)
     * @return
     */
    public List<GeoRadiusResponse> georadiusByMember(final String key, final String member, final double radius, final GeoUnit unit) {
        return call(new ICacheAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(IJedisCache jedis) {
                return jedis.georadiusByMember(key, member, radius, unit);
            }
        });
    }

    /**
     * 订阅消息
     * @param listener			订阅监听器
     * @param channels		订阅渠道
     */
    /*
    public static void subscribe2(final RedisListener listener, final List<String> channels ) {
		if (channels.isEmpty())
			throw new NullPointerException("channels is null");
		final CountDownLatch latch = new CountDownLatch(1);
		final String[] channelsArray = channels.toArray(new String[] {});
		try {
		ThreadPool.execute(new Thread() {
			public void run() {
		call(new ICacheAction<Boolean>() {
			public Boolean execute(final IJedisCache jedis) {
				if (isCluster(jedisObj)) {
					try {
						ThreadPool.execute(new Thread() {
							public void run() {
								c2jc(jedisObj).subscribe(listener, channelsArray);
							}
						});
						latch.countDown();
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				} else {
					try {
						ThreadPool.execute(new Thread() {
							public void run() {
								c2j(jedisObj).subscribe(listener, channelsArray);
							}
						});
						latch.countDown();
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				try {
					latch.await();
					logger.warn("#############: subscribe " + channels + " done!");
					return true;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
			}
		});
		}catch(Exception e){
			e.printStackTrace();
		}
    }

    public static void subscribe3(final RedisListener listener, final List<String> channels) {
    	if (channels.isEmpty())
			throw new NullPointerException("channels is null");
		try {
			ThreadPool.execute(new Thread() {
				public void run() {
					call(new ICacheAction<Boolean>() {
						public Boolean execute(final IJedisCache jedis) {
							final String[] channelsArray = channels.toArray(new String[] {});
							logger.warn("#############: subscribe " + channels + " done!");
							if (isCluster(jedisObj)) {
								c2jc(jedisObj).subscribe(listener, channelsArray);
							} else {
								c2j(jedisObj).subscribe(listener, channelsArray);
							}
							return true;
						}
					});
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
    */
    /**
     * 订阅消息
     * @param listener			订阅监听器
     * @param channels		订阅渠道
     */
    public static void subscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    if(!isCluster()) {
                        Jedis jedis = JedisPoolUtils.getJedis();
                        jedis.subscribe(listener, channelsArray);
                    } else {
                        JedisCluster jedisCluster = JedisClusterPoolUtils.getJedisCluster();
                        jedisCluster.subscribe(listener, channelsArray);
                    }
                }
            });
        } catch (Exception e1) {
            logger.warn(e1.getMessage(), e1);
        } finally {
            logger.warn("#############: subscribe " + channels + " done!");
        }
    }

    /**
     * 模式匹配方式订阅消息
     * @param listener			订阅监听器
     * @param channels		订阅渠道
     * @return
     */
    public static void psubscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    if(!isCluster()) {
                        Jedis jedis = JedisPoolUtils.getJedis();
                        jedis.psubscribe(listener, channelsArray);
                    } else {
                        JedisCluster jedisCluster = JedisClusterPoolUtils.getJedisCluster();
                        jedisCluster.psubscribe(listener, channelsArray);
                    }
                }
            });
        } catch (Exception e1) {
            logger.warn(e1.getMessage(), e1);
        } finally {
            logger.warn("#############: psubscribe " + channels + " done!");
        }
    }

    /**
     * 发布消息
     * @param message
     * @return
     */
    public long publish(final RedisMessage message ) {
        return call(new ICacheAction<Long>() {
            @Override
            public Long execute(IJedisCache jedis) {
                byte[] bytes = SerializableUtils.serializeByte(message.getBody());
                try {
                    return jedis.publish(message.getChannel(), new String(bytes, Protocol.CHARSET));
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    return 0L;
                }
            }
        });
    }
}
