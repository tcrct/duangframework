package com.duangframework.mongodb.common;

import com.duangframework.core.kit.ConfigKit;

import java.util.List;

/**
 * MongoDB的链接信息对象
 *
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoConnect implements java.io.Serializable {

    /**
     *数据库
     */
    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "mongoDatabase";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String REPLICASET_FIELD = "repliCaset";

    private String host;
    private int port;
    private String dataBase;
    private String userName;
    private String passWord;
    private List<String> repliCaset;

    public MongoConnect() {
        this( ConfigKit.duang().key("mongodb.host").defaultValue("127.0.0.1").asString(),
                ConfigKit.duang().key("mongodb.port").defaultValue("27017").asInt(),
                ConfigKit.duang().key("mongodb.databasename").defaultValue("local").asString(), null,null, null);
    }

    public MongoConnect(String host, int port, String dataBase) {
        this(host, port, dataBase, null,null, null);
    }

    public MongoConnect(String host, int port, String dataBase, String userName, String passWord, List<String> repliCaset) {
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.userName = userName;
        this.passWord = passWord;
        this.repliCaset = repliCaset;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public List<String> getRepliCaset() {
        return repliCaset;
    }

    public void setRepliCaset(List<String> repliCaset) {
        this.repliCaset = repliCaset;
    }



}
