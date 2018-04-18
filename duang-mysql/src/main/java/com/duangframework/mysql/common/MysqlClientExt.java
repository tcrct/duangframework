package com.duangframework.mysql.common;

import javax.sql.DataSource;

/**
 * @author Created by laotang
 * @date createed in 2018/4/17.
 */
public class MysqlClientExt {

    private String key;
    private DataSource client;
    private MysqlDbConnect connect;

    public MysqlClientExt() {
    }

    public MysqlClientExt(String key, DataSource client, MysqlDbConnect connect) {
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

    public DataSource getClient() {
        return client;
    }

    public void setClient(DataSource client) {
        this.client = client;
    }

    public MysqlDbConnect getConnect() {
        return connect;
    }

    public void setConnect(MysqlDbConnect connect) {
        this.connect = connect;
    }
}
