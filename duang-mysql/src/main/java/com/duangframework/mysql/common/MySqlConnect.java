package com.duangframework.mysql.common;

import com.duangframework.core.common.DBConnect;
import com.duangframework.mysql.core.ds.DruidDataSourceFactory;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MySqlConnect  extends DBConnect  implements IConnect {

    public MySqlConnect(String host, int port, String userName, String passWord, String dataBaseName) {
        super(host, port, dataBaseName, userName, passWord);
    }

    @Override
    public String getJdbcUrl() {
        String host = this.getHost().toLowerCase().replace(PROTOCOL,"").replace(PROTOCOLS,"").replace("*","");
        int endIndex = host.indexOf(":");
        host = host.substring(0, endIndex > -1 ? endIndex : host.length());
        return "jdbc:mysql://"+host+":"+getPort()+"/"+getDataBase();
    }


    @Override
    public String getDataSourceFactoryClassName() {
        return DruidDataSourceFactory.class.getName();
    }

}
