package com.duangframework.mongodb.common;

import com.duangframework.core.common.DBConnect;
import com.duangframework.core.kit.PropertiesKit;

import java.util.List;

/**
 * MongoDB的链接信息对象
 *
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoConnect extends DBConnect {

    /**
     *数据库
     */
    public static final String REPLICASET_FIELD = "repliCaset";

    private List<String> repliCaset;

    public MongoConnect() {
        this( PropertiesKit.duang().key("mongodb.host").defaultValue("127.0.0.1").asString(),
                PropertiesKit.duang().key("mongodb.port").defaultValue("27017").asInt(),
                PropertiesKit.duang().key("mongodb.databasename").defaultValue("local").asString(), null,null, null);
    }

    public MongoConnect(String host, int port, String dataBase) {
        this(host, port, dataBase, null,null, null);
    }

    public MongoConnect(String host, int port, String dataBase, String userName, String passWord, List<String> repliCaset) {
        super(host, port, dataBase, userName, passWord);
        this.repliCaset = repliCaset;
    }

    public List<String> getRepliCaset() {
        return repliCaset;
    }

    public void setRepliCaset(List<String> repliCaset) {
        this.repliCaset = repliCaset;
    }



}
