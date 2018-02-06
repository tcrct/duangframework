package com.duangframework.ext.crud;

/**
 * @author Created by laotang
 * @date createed in 2018/2/6.
 */
public class EntityCacheModle {

    /**
     * 缓存key字段
     */
    private String key;
    /**
     * 有效时间
     */
    private int ttl;

    public EntityCacheModle() {
    }

    public EntityCacheModle(String key, int ttl) {
        this.key = key;
        this.ttl = ttl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
