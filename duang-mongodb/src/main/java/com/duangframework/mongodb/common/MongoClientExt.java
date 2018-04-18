package com.duangframework.mongodb.common;

import com.mongodb.MongoClient;

/**
 * @author Created by laotang
 * @date createed in 2018/4/17.
 */
public class MongoClientExt {

    private String key;
    private MongoClient client;
    private MongoDbConnect connect;

    public MongoClientExt() {
    }

    public MongoClientExt(String key, MongoClient client, MongoDbConnect connect) {
        this.key = key;
        this.client = client;
        this.connect = connect;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MongoClient getClient() {
        return client;
    }

    public void setClient(MongoClient client) {
        this.client = client;
    }

    public MongoDbConnect getConnect() {
        return connect;
    }

    public void setConnect(MongoDbConnect connect) {
        this.connect = connect;
    }
}
