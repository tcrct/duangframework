package com.duangframework.mysql.common;

import com.duangframework.core.common.DBConnect;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MySqlConnect  extends DBConnect  implements IConnect {

    private String jdbcUrl;
    private String dataSourceFactoryClassName;


    public MySqlConnect(String userName, String passWord, String jdbcUrl, String dataSourceFactoryClassName) {
        super("127.0.0.1", 3306, "", userName, passWord);
        this.jdbcUrl = jdbcUrl;
        this.dataSourceFactoryClassName = dataSourceFactoryClassName;
    }

    @Override
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public String getDataSourceFactoryClassName() {
        return dataSourceFactoryClassName;
    }

}
