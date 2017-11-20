package com.duangframework.mongodb.common;

import java.util.List;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoConnect implements java.io.Serializable {

    /**
     *数据库
     */
    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "database";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String REPLICASET_FIELD = "replicaset";

    private String host;
    private int port;
    private String dataBase;
    private String userName;
    private String passWord;
    private List<String> replicaset;

    public MongoConnect(String host, int port, String dataBase, String userName, String passWord, List<String> replicaset) {
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.userName = userName;
        this.passWord = passWord;
        this.replicaset = replicaset;
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

    public List<String> getReplicaset() {
        return replicaset;
    }

    public void setReplicaset(List<String> replicaset) {
        this.replicaset = replicaset;
    }



}
