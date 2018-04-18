package com.duangframework.mysql.common;

import com.duangframework.core.common.ConfigValue;
import com.duangframework.core.common.db.AbstractDbConnect;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mysql.core.ds.DruidDataSourceFactory;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/4/17.
 */
public class MysqlDbConnect extends AbstractDbConnect {

    public MysqlDbConnect() {
    }

    public MysqlDbConnect(List<ConfigValue> valueList) {
        super(valueList);
    }

    public MysqlDbConnect(String host, int port, String dataBase, String userName, String passWord) {
        this(host, port, dataBase, userName, passWord, "");
    }

    public MysqlDbConnect(String host, int port, String dataBase, String userName, String passWord, String clientCode) {
        this(host, port, dataBase, userName, passWord, "", clientCode);
    }

    public MysqlDbConnect(String url) {
        this(url ,"");
    }

    public MysqlDbConnect(String url, String clientCode) {
        this("", 0, "", "", "", url, clientCode);
    }

    public MysqlDbConnect(String host, int port, String dataBase, String userName, String passWord, String url, String clientCode) {
        super(host, port, dataBase, userName, passWord, url, clientCode);
        setDataSourceFactoryClassName(DruidDataSourceFactory.class.getName());
    }

    @Override
    public String getUrl() {
        if(ToolsKit.isEmpty(url)) {
            String host = this.getHost().toLowerCase().replace(PROTOCOL, "").replace(PROTOCOLS, "").replace("*", "");
            int endIndex = host.indexOf(":");
            host = host.substring(0, endIndex > -1 ? endIndex : host.length());
            return "jdbc:mysql://" + host + ":" + getPort() + "/" + getDataBase();
        }
        return url;
    }
}
