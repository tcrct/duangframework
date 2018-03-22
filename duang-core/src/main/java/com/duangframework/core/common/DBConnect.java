package com.duangframework.core.common;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class DBConnect implements java.io.Serializable {

    /**
     *数据库
     */
    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "dataBase";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    protected final static String PROTOCOL = "http://";
    protected final static String PROTOCOLS = "https://";

    private String host;
    private int port;
    private String dataBase;
    private String userName;
    private String passWord;


    public DBConnect() {

    }

    public DBConnect(String host, int port, String dataBase, String userName, String passWord) {
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.userName = userName;
        this.passWord = passWord;
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
}
