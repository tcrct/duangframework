package com.duangframework.mysql.common;

import com.duangframework.core.common.DBConnect;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MySqlConnect  extends DBConnect  implements IConnect {

    private String jdbcUrl;

    public MySqlConnect(String host, int port, String dataBase) {
        this(host, port, dataBase, null,null, null);
    }

    public MySqlConnect(String host, int port, String dataBase, String userName, String passWord, String jdbcUrl) {
        super(host, port, dataBase, userName, passWord);
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
}
