package com.duangframework.cache.sdk.redis;

import com.alibaba.fastjson.TypeReference;
import com.duangframework.cache.common.AbstractRedisClient;
import com.duangframework.cache.common.JedisAction;
import com.duangframework.cache.utils.CacheUtils;
import com.duangframework.cache.utils.SerializableUtils;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.util.SafeEncoder;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class RedisClient extends AbstractRedisClient {

    private final static Logger logger = LoggerFactory.getLogger(RedisClient.class);
    private static RedisClient ourInstance;

    public static RedisClient duang() {
        try {
            if (null == ourInstance) {
                ourInstance = new RedisClient();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        use(CacheUtils.getDefaultClientExt().getKey());
        return ourInstance;
    }

    private RedisClient() {

    }

    public static RedisClient use(String key) {
       setCacheClientExt(CacheUtils.getCacheClientExt(key));
        return ourInstance;
    }


    /**
     * 是否集群对象
     * @param jedisObj  jedis对象
     * @return   是集群返回true
     */
    private static boolean isCluster(Object jedisObj) {
        return (jedisObj instanceof JedisCluster)  ? true : false;
    }
    private Jedis c2j(Object jedisObj) {
        return (Jedis) jedisObj;
    }
    private JedisCluster c2jc(Object jedisObj) {
        return (JedisCluster) jedisObj;
    }
    private byte[] getBytes4Cluster(String value){
        return ToolsKit.isEmpty(value) ? null : value.getBytes();
    }

    /**
     * 根据key取值
     * @param key
     * @return
     */
    public <T> T get(final String key, final Class<T> typeReference){
        return call(new JedisAction<T>(){            
            @Override
            public T execute(Object jedisObj) {
                byte[] bytes = (isCluster(jedisObj)) ? getBytes4Cluster(c2jc(jedisObj).get(key)) :  c2j( jedisObj).get(SafeEncoder.encode(key));
                if(ToolsKit.isNotEmpty(bytes)){
                    try {
                        String str = new String(bytes, Const.ENCODING_FIELD);
                        if(typeReference.equals(String.class)){
                            return (T)str;
                        } else if(typeReference.equals(Integer.class) || typeReference.equals(int.class)){
                            return (T)new Integer(str);
                        } else if(typeReference.equals(Long.class) || typeReference.equals(long.class)){
                            return (T)new Long(str);
                        } else if(typeReference.equals(Double.class) || typeReference.equals(double.class)){
                            return (T)new Double(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new JedisException(e.getMessage());
                    }
                    return (T)SerializableUtils.deserialize(bytes, typeReference);
                }
                return null;
            }
        });
    }

    /**
     *  取值
     * @param key
     * @param type		泛型
     * @return
     */
    public <T> T get(final String key, final TypeReference<T> type){
        return call(new JedisAction<T>(){
            @Override
            public T execute(Object jedisObj) {
                byte[] bytes = (isCluster(jedisObj)) ? getBytes4Cluster(c2jc(jedisObj).get(key)) :  c2j( jedisObj).get(SafeEncoder.encode(key));
                if(ToolsKit.isNotEmpty(bytes)){
                    return (T)SerializableUtils.deserialize(bytes, type);
                }
                return null;
            }
        });
    }


    public <T> List<T> getArray(final String key, final Class<T> typeReference){
        return call(new JedisAction<List<T>>(){
            @Override
            public List<T> execute(Object jedisObj) {
                byte[] bytes = (!isCluster(jedisObj)) ? c2j(jedisObj).get(SafeEncoder.encode(key)) :  getBytes4Cluster(c2jc(jedisObj).get(key));
                if(ToolsKit.isNotEmpty(bytes)){
                    try {
                        return (List<T>)SerializableUtils.deserializeArray(bytes, typeReference);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new JedisException(e.getMessage());
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
    public boolean set(final String key, final Object value){
        if(null == value){
            logger.warn("JedisUtils.set value is null, return false...");
            return false;
        }
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Object jedisObj) {
                String result = "";
                if(value instanceof String){
                    result = isCluster(jedisObj) ? c2jc(jedisObj).set(key, (String)value)  : c2j(jedisObj).set(key, (String) value);
                }else{
                    result = isCluster(jedisObj) ? c2jc(jedisObj).set(key, SerializableUtils.serializeString(value)) : c2j(jedisObj).set(SafeEncoder.encode(key), SerializableUtils.serialize(value));
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
    public boolean set(final String key, final Object value, final int seconds){
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Object jedisObj) {
                String result = "";
                if(value instanceof String){
                    result = isCluster(jedisObj) ? c2jc(jedisObj).setex(key, seconds, (String)value)  : c2j(jedisObj).setex(key, seconds,  (String) value);
                }else{
                    result = isCluster(jedisObj) ? c2jc(jedisObj).setex(key, seconds, SerializableUtils.serializeString(value)) : c2j(jedisObj).setex(SafeEncoder.encode(key), seconds, SerializableUtils.serialize(value));
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
    public long del(final String... keys){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                if(isCluster(jedisObj)){
                    try{
                        for(String keyItem : keys){
                            c2jc(jedisObj).del(keyItem);
                        }
                        return 1L;
                    }catch(Exception e){
                        return 0L;
                    }
                } else {
                    return c2j(jedisObj).del(keys);
                }
            }
        });
    }


    /**
     * 将内容添加到list里的第一位
     * @param key		关键字
     * @param value		内容
     * @return
     */
    public long lpush(final String key, final Object value) {
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                if(value instanceof String){
                    return isCluster(jedisObj) ? c2jc(jedisObj).lpush(key,(String)value) : c2j(jedisObj).lpush(key, (String)value);
                }else{
                    return isCluster(jedisObj) ? c2jc(jedisObj).lpush(key,SerializableUtils.serializeString(value)) : c2j(jedisObj).lpush(SafeEncoder.encode(key), SerializableUtils.serialize(value));
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                if(value instanceof String){
                    return isCluster(jedisObj) ? c2jc(jedisObj).rpush(key, SerializableUtils.serializeString(value)) : c2j(jedisObj).rpush(key, (String)value);
                }else{
                    return isCluster(jedisObj) ? c2jc(jedisObj).rpush(key, SerializableUtils.serializeString(value)) : c2j(jedisObj).rpush(SafeEncoder.encode(key), SerializableUtils.serialize(value));
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
        return call(new JedisAction<List<String>>(){
            @Override
            public List<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).lrange(key, start, end): c2j(jedisObj).lrange(key, start, end);
            }
        });
    }

    public long lrem(final String key, final int count, final Object value){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                if(value instanceof String){
                    return isCluster(jedisObj) ? c2jc(jedisObj).lrem(key, count, (String)value) : c2j(jedisObj).lrem(key, count, (String)value);
                }else{
                    return  isCluster(jedisObj) ? c2jc(jedisObj).lrem(key, count, SerializableUtils.serializeString(value)) : c2j(jedisObj).lrem(SafeEncoder.encode(key), count, SerializableUtils.serialize(value));
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
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Object jedisObj) {
                boolean isCluster =  isCluster(jedisObj);
                String isok = "";
                if(null != values){
                    if(!isCluster){
                        Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(values.size());
                        for (Iterator<Map.Entry<String,String>> it = values.entrySet().iterator(); it.hasNext(); ){
                            Map.Entry<String,String> entry = it.next();
                            map.put(SafeEncoder.encode(entry.getKey()), SafeEncoder.encode(entry.getValue()));
                        }
                        isok = c2j(jedisObj).hmset(SafeEncoder.encode(key), map);
                    } else {
                        isok = c2jc(jedisObj).hmset(key, values);
                    }
                    return "OK".equalsIgnoreCase(isok);
                }
                return false;
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
        return call(new JedisAction<List<String>>() {
            @Override
            public List<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hmget(key, fields): c2j(jedisObj).hmget(key, fields);
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
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String, String> execute(Object jedisObj) {
                List<String> byteList = isCluster(jedisObj) ? c2jc(jedisObj).hmget(key, fields) : c2j(jedisObj).hmget(key, fields);
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
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Object jedisObj) {
                if(isCluster(jedisObj)){
                    return c2jc(jedisObj).hdel(key, fields);
                } else {
                    byte[][] byteFields = new byte[fields.length][];
                    for (int i = 0; i < fields.length; i++) {
                        byteFields[i] = SafeEncoder.encode(fields[i]);
                    }
                    return c2j(jedisObj).hdel(SafeEncoder.encode(key), byteFields);
                }
            }
        });
    }

    /**
     * 取出指定hash里的所有field
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hkeys(key) : c2j(jedisObj).hkeys(key);
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
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Object jedisObj) {
                boolean isok = false;
                if (null != field) {
                    return isCluster(jedisObj) ? c2jc(jedisObj).hexists(key, field) : c2j(jedisObj).hexists(key,  field);
                }
                return isok;
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
        return call(new JedisAction<String>() {
            @Override
            public String execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hget(key, field) : c2j(jedisObj).hget(key,  field);
            }
        });
    }

    /**
     * key返回哈希表key中，所有的域和值
     * @param key
     * @return
     */
    public Map<String,String> hgetAll(final String key) {
        return call(new JedisAction<Map<String,String>>() {
            @Override
            public Map<String,String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hgetAll(key) : c2j(jedisObj).hgetAll(key);
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
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Object jedisObj) {
                long isok = 0;
                if(value instanceof String){
                    isok = isCluster(jedisObj) ? c2jc(jedisObj).sadd(key, (String)value) : c2j(jedisObj).sadd(key, (String)value);
                }else{
                    isok = isCluster(jedisObj) ? c2jc(jedisObj).sadd(key, SerializableUtils.serializeString(value)) : c2j(jedisObj).sadd(SafeEncoder.encode(key), SerializableUtils.serialize(value));
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).scard(key) : c2j(jedisObj).scard(key);
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
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).sismember(key, (String)value) : c2j(jedisObj).sismember(key, (String)value);
            }
        });
    }

    /**
     * 慎用，会导致redis等待结果返回，若是集群模式则直接返回null
     * @param pattern		正则表达式
     * @return
     */
    public Set<String> keys(final String pattern){
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? null : c2j(jedisObj).keys(pattern);
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                if("list".equalsIgnoreCase(type)){
                    return isCluster(jedisObj) ? c2jc(jedisObj).llen(key) : c2j(jedisObj).llen(key);
                }else if("hash".equalsIgnoreCase(type)){
                    return isCluster(jedisObj) ? c2jc(jedisObj).hlen(key) : c2j(jedisObj).hlen(key);
                }else if("set".equalsIgnoreCase(type)){
                    return isCluster(jedisObj) ? c2jc(jedisObj).scard(key) : c2j(jedisObj).scard(key);
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
        return call(new JedisAction<String>() {
            @Override
            public String execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).type(key) : c2j(jedisObj).type(key);
            }
        });
    }

    /**
     * 判断KEY是否存在
     * @param key		关键字
     * @return			存在返回true
     */
    public boolean exists(final String key) {
        return call(new JedisAction<Boolean>() {
            @Override
            public Boolean execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).exists(key) : c2j(jedisObj).exists(key);
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).expire(key, seconds) : c2j(jedisObj).expire(key, seconds);
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
        return call(new JedisAction<Boolean>(){
            @Override
            public Boolean execute(Object jedisObj) {
                try {
                    if(isCluster(jedisObj)) {
                        c2jc(jedisObj).zadd(key, sort, value);
                    } else {
                        c2j(jedisObj).zadd(key, sort, value);
                    }
                    return true;
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
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrem(key, value) : c2j(jedisObj).zrem(key, value);
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
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrank(key, member) : c2j(jedisObj).zrank(key, member);
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
        return call(new JedisAction<Long >(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrevrank(key, member) : c2j(jedisObj).zrevrank(key, member);
            }
        });
    }

    /**
     * 升序获取zset元素
     * @param key
     * @return
     */
    public List<String> zrevrank(final String key){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Object jedisObj) {
                if(isCluster(jedisObj)){
                    return new ArrayList<String>(c2jc(jedisObj).zrange(key, 0, -1));
                } else {
                    return new ArrayList<String>( ((Jedis)jedisObj).zrange(key, 0, -1) );
                }
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
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Object jedisObj) {
                int e = end;
                if(e > 0){e--;}
                if(isCluster(jedisObj)){
                    return new ArrayList<String>(c2jc(jedisObj).zrange(key, start, e));
                } else {
                    return new ArrayList<String>( c2j(jedisObj).zrange(key, start, e) );
                }
            }
        });
    }

    /**
     * 降序获取zset元素
     * @param key
     * @return
     */
    public List<String> zrevrange(final String key){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Object jedisObj) {
                if(isCluster(jedisObj)){
                    return new ArrayList<String>( c2jc(jedisObj).zrevrange(key, 0, -1) );
                } else {
                    return new ArrayList<String>( c2j(jedisObj).zrevrange(key, 0, -1) );
                }
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
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Object jedisObj) {
                int e = end;
                if(e > 0){e--;}
                if(isCluster(jedisObj)){
                    return new ArrayList<String>( c2jc(jedisObj).zrevrange(key,start, e) );
                } else {
                    return new ArrayList<String>( c2j(jedisObj).zrevrange(key,start, e) );
                }
            }
        });
    }

    public List<String> zrange(final String key,final int start, final int end){
        return call(new JedisAction<List<String> >(){
            @Override
            public List<String> execute(Object jedisObj) {
                if(isCluster(jedisObj)){
                    return new ArrayList<String>( c2jc(jedisObj).zrange(key, start, end) );
                } else {
                    return new ArrayList<String>( c2j(jedisObj).zrange(key, start, end) );
                }
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
        return call(new JedisAction<Set<Tuple>>() {
            @Override
            public Set<Tuple> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrevrangeWithScores(key, start, end) : c2j(jedisObj).zrevrangeWithScores(key, start, end);
            }
        });
    }

    /**
     * 根据key获取list长度
     * @param key
     * @return
     */
    public Long llen(final String key){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).llen(key) : c2j(jedisObj).llen(key);
            }
        });
    }

    /**
     * 根据key删除并返回list尾元素
     * @param key
     * @returnrpop
     */
    public String rpop(final String key){
        return call(new JedisAction<String>(){
            @Override
            public String  execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).rpop(key) : c2j(jedisObj).rpop(key);
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hincrBy(key, field, integer) : c2j(jedisObj).hincrBy(key, field, integer);
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
        return call(new JedisAction<Long >(){
            @Override
            public Long  execute(Object jedisObj) {
                if(value instanceof String){
                    return isCluster(jedisObj) ? c2jc(jedisObj).hset(key, field, (String)value) : c2j(jedisObj).hset(key, field, (String)value);
                } else {
                    return isCluster(jedisObj) ? c2jc(jedisObj).hset(key, field, SerializableUtils.serializeString(value)) : c2j(jedisObj).hset(SafeEncoder.encode(key), SafeEncoder.encode(field), SerializableUtils.serialize(value));
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
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrangeByScore(key, min, max) : c2j(jedisObj).zrangeByScore(key, min, max);
            }
        });
    }

    /**
     * 返回名称为key的zset中score>=min且score<=max结果之间的区间数据 <br/>
     *  offset, count就相当于sql中limit的用法 <br/>
     *  select * from table where score >=min and score <=max limit offset count
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrangebyscore(final String key, final double min, final double max, final int offset, final int count) {
        return call(new JedisAction<Set<String>>() {
            @Override
            public Set<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zrangeByScore(key, min, max, offset, count) : c2j(jedisObj).zrangeByScore(key, min, max, offset, count);
            }
        });
    }


    /**
     * 返回名称为key的zset中score>=min且score<=max结果之间的区间数据 <br/>
     *  offset, count就相当于sql中limit的用法 <br/>
     *  select * from table where score >=min and score <=max limit offset count
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public List<TupleDto> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        return call(new JedisAction<List<TupleDto>>() {
            @Override
            public List<TupleDto> execute(Object jedisObj) {
                Set<Tuple> tupleSet = isCluster(jedisObj) ? c2jc(jedisObj).zrangeByScoreWithScores(key, min, max, offset, count) : c2j(jedisObj).zrangeByScoreWithScores(key, min, max, offset, count);
                List<TupleDto> tupleDtoList = new ArrayList<>();
                if(ToolsKit.isNotEmpty(tupleSet)) {
                    for(Tuple tuple : tupleSet) {
                        tupleDtoList.add(new TupleDto(tuple.getElement(), BigDecimal.valueOf(tuple.getScore()).doubleValue()));
                    }
                }
                return tupleDtoList;
            }
        });
    }


    /**
     * 删除名称为key的zset中score>=min且score<=max的所有元素
     */
    public Long zremrangebyscore(final String key, final double min, final double max) {
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zremrangeByScore(key, min, max) : c2j(jedisObj).zremrangeByScore(key, min, max);
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zremrangeByRank(key, start, max) : c2j(jedisObj).zremrangeByRank(SafeEncoder.encode(key), start, max);
            }
        });
    }


    /**
     * 为某个key自增1
     * @param key
     * @return
     */
    public Long incr(final String key){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).incr(key) : c2j(jedisObj).incr(SafeEncoder.encode(key));
            }
        });
    }

    /**
     * 为某个key自减1
     * @param key
     * @return
     */
    public Long decr(final String key){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).decr(key) : c2j(jedisObj).decr(SafeEncoder.encode(key));
            }
        });
    }

    /**
     * 获取set的基数
     * @param key
     * @return
     */
    public Long zcard(final String key){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zcard(key) : c2j(jedisObj).zcard(SafeEncoder.encode(key));
            }
        });
    }

    /**
     * 返回key的有效时间
     * @param key
     * @return
     */
    public Long ttl(final String key){
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).ttl(key) : c2j(jedisObj).ttl(SafeEncoder.encode(key));
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
        return call(new JedisAction<Long>(){
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).srem(key, member) : c2j(jedisObj).srem(key, member);
            }
        });
    }

    /**
     * 获取set对象
     * @param key
     * @return
     */
    public Set<String> smembers(final String key){
        return call(new JedisAction<Set<String>>(){
            @Override
            public Set<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).smembers(key) : c2j(jedisObj).smembers(key);
            }
        });
    }

    /**
     * 获取zset里面元素的soce
     * @param key
     * @return
     */
    public Double zscore(final String key,final String member){
        return call(new JedisAction<Double>(){
            @Override
            public Double execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).zscore(key, member) : c2j(jedisObj).zscore(key, member);
            }
        });
    }

    /**
     * 返回 key 指定的哈希集中所有字段的值
     * @param key
     * @return
     */
    public List<String> hvals(final String key){
        return call(new JedisAction<List<String>>(){
            @Override
            public List<String> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).hvals(key) : c2j(jedisObj).hvals(key);
            }
        });
    }

    @SuppressWarnings("unused")
    private <T> T batctGet(final Set<String> keys) {
        return call(new JedisAction<T>(){
            @Override
            @SuppressWarnings("unchecked")
            public T execute(Object jedisObj){
                Map<String,String> result = new HashMap<String, String>(keys.size());
                if(isCluster(jedisObj)) {
                    for(String key : keys){
                        result.put(key, c2jc(jedisObj).get(key));
                    }
                } else {
                    Pipeline p = c2j(jedisObj).pipelined();
                    Map<String,Response<Map<String,String>>> responses = new HashMap<String,Response<Map<String,String>>>(keys.size());
                    for(String key : keys) {
                        responses.put(key, p.hgetAll(key));
                    }
                    for(Iterator<String> it = responses.keySet().iterator(); it.hasNext();){
                        String key = it.next();
                        result.put(key, responses.get(key).get().get(key));
                    }
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
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).geoadd(key, longitude, latitude, member) : c2j(jedisObj).geoadd(key, longitude, latitude, member);
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
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).geoadd(key, memberCoordinateMap) : c2j(jedisObj).geoadd(key, memberCoordinateMap);
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
        return call(new JedisAction<List<GeoCoordinate>>() {
            @Override
            public List<GeoCoordinate> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).geopos(key, members) : c2j(jedisObj).geopos(key, members);
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
        return call(new JedisAction<Double>() {
            @Override
            public Double execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).geodist(key, member1, member2, unit) : c2j(jedisObj).geodist(key, member1, member2, unit);
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
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).georadius(key, longitude, latitude, radius, unit) : c2j(jedisObj).georadius(key, longitude, latitude, radius, unit);
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
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).georadius(key, longitude, latitude, radius, unit, param) : c2j(jedisObj).georadius(key, longitude, latitude, radius, unit, param);
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
        return call(new JedisAction<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> execute(Object jedisObj) {
                return isCluster(jedisObj) ? c2jc(jedisObj).georadiusByMember(key, member, radius, unit) : c2j(jedisObj).georadiusByMember(key, member, radius, unit);
            }
        });
    }

/**
 * 订阅消息
 * @param listener			订阅监听器
 * @param channels		订阅渠道
 */
/*
public void subscribe2(final RedisListener listener, final List<String> channels ) {
    if (channels.isEmpty())
        throw new NullPointerException("channels is null");
    final CountDownLatch latch = new CountDownLatch(1);
    final String[] channelsArray = channels.toArray(new String[] {});
    try {
    ThreadPool.execute(new Thread() {
        public void run() {
    call(new JedisAction<Boolean>() {
        public Boolean execute(final Object jedisObj) {
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

public void subscribe3(final RedisListener listener, final List<String> channels) {
    if (channels.isEmpty())
        throw new NullPointerException("channels is null");
    try {
        ThreadPool.execute(new Thread() {
            public void run() {
                call(new JedisAction<Boolean>() {
                    public Boolean execute(final Object jedisObj) {
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
    public void subscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    if(!RedisClient.isCluster()) {
                        Jedis jedis = getJedis();
                        jedis.subscribe(listener, channelsArray);
                    } else {
                        JedisCluster jedisCluster = getJedisCluster();
                        jedisCluster.subscribe(listener, channelsArray);
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
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
    public void psubscribe(final RedisListener listener, final List<String> channels) {
        if (channels.isEmpty()) {
            throw new NullPointerException("channels is null");
        }
        try {
            final String[] channelsArray = channels.toArray(new String[] {});
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    if(!RedisClient.isCluster()) {
                        Jedis jedis = getJedis();
                        jedis.psubscribe(listener, channelsArray);
                    } else {
                        JedisCluster jedisCluster = getJedisCluster();
                        jedisCluster.psubscribe(listener, channelsArray);
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
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
        return call(new JedisAction<Long>() {
            @Override
            public Long execute(Object jedisObj) {
                byte[] channel = SafeEncoder.encode(message.getChannel());
                byte[] bytes = SerializableUtils.serialize(message.getBody());
                return isCluster(jedisObj) ? c2jc(jedisObj).publish(channel, bytes) : c2j(jedisObj).publish(channel, bytes);
            }
        });
    }
}
