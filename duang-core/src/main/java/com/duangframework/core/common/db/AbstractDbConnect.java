package com.duangframework.core.common.db;

import com.duangframework.core.common.ConfigValue;
import com.duangframework.core.interfaces.IConnect;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/4/17.
 */
public abstract class AbstractDbConnect implements IConnect {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDbConnect.class);

    /**
     *数据库
     */
    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "database";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String URL_FIELD = "url";
    public static final String CLIENT_CODE_FIELD = "clientcode";
    protected final static String PROTOCOL = "http://";
    protected final static String PROTOCOLS = "https://";


    protected String host;
    protected int port;
    protected String dataBase;
    protected String userName;
    protected String passWord;
    protected String url;
    protected String clientCode;
    protected String dataSourceFactoryClassName;
    protected boolean defaultClient;

    public AbstractDbConnect() {
    }

    public AbstractDbConnect(List<ConfigValue> valueList) {
        for (ConfigValue configValue : valueList) {
            String key = configValue.getKey().toLowerCase();
            if(ToolsKit.isEmpty(key)) {
                continue;
            }
            if(key.toLowerCase().endsWith(HOST_FIELD.toLowerCase())) {
                host = (String)configValue.getConfigValue();
            }
            else if(key.endsWith(PORT_FIELD.toLowerCase())) {
                try {
                    port = Integer.parseInt((String) configValue.getConfigValue());
                } catch (Exception e) { e.printStackTrace();}
            }
            else if(key.toLowerCase().endsWith(DATABASE_FIELD.toLowerCase())) {
                dataBase = (String)configValue.getConfigValue();
            }
            else if(key.endsWith(USERNAME_FIELD.toLowerCase())) {
                userName = (String)configValue.getConfigValue();
            }
            else if(key.endsWith(PASSWORD_FIELD.toLowerCase())) {
                passWord = (String)configValue.getConfigValue();
            }
            else if(key.endsWith(URL_FIELD.toLowerCase())) {
                url = (String)configValue.getConfigValue();
                setUrl(url);
            }
            else if(key.endsWith(CLIENT_CODE_FIELD.toLowerCase())) {
                clientCode = (String)configValue.getConfigValue();
            }
        }
    }

    public AbstractDbConnect(String host, int port, String dataBase, String userName, String passWord) {
       this(host, port, dataBase, userName, passWord, "", "");
        this.clientCode = getClientCode();
    }

    public AbstractDbConnect(String host, int port, String dataBase, String userName, String passWord, String url) {
        this(host, port, dataBase, userName, passWord, url, "");
        this.clientCode = getClientCode();
    }

    public AbstractDbConnect(String host, int port, String dataBase, String userName, String passWord, String url, String clientCode) {
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.userName = userName;
        this.passWord = passWord;
        setUrl(url);
        this.clientCode = clientCode;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if(ToolsKit.isNotEmpty(url) && (url.length() < 10)) {
            url = "";
        }
        this.url = url;
    }

    public String getClientCode() {
        if(ToolsKit.isEmpty(clientCode)) {
            clientCode = toCodeMD5String();
        }
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    @Override
    public String getDataSourceFactoryClassName() {
        return dataSourceFactoryClassName;
    }

    public void setDataSourceFactoryClassName(String dataSourceFactoryClassName) {
        this.dataSourceFactoryClassName = dataSourceFactoryClassName;
    }

    private String toCodeMD5String() {
        if(ToolsKit.isNotEmpty(url)) {
            return MD5.MD5Encode(url);
        }
        else if(ToolsKit.isNotEmpty(host)) {
            return MD5.MD5Encode("tcrct:" + host + ":" + port);
        }
        return "";
    }


    public void printDbInfo(String clientCode) {
        this.setClientCode(clientCode);
        logger.warn("clientCode:  " + getClientCode() + "  values: " + ToolsKit.toJsonString(this));
    }

    public boolean isDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(boolean defaultClient) {
        this.defaultClient = defaultClient;
    }
}
