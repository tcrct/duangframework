package com.duangframework.cache.common;

import com.duangframework.core.common.ConfigValue;
import com.duangframework.core.common.db.AbstractDbConnect;
import com.duangframework.core.kit.ToolsKit;

import java.util.Arrays;
import java.util.List;

/**
 * MongoDB的链接信息对象
 *
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class CacheDbConnect extends AbstractDbConnect {

    /**
     *数据库
     */
    public static final String REPLICASET_FIELD = "repliCaset";

    private List<String> repliCaset;

    public CacheDbConnect() {
    }

    public CacheDbConnect(List<ConfigValue> valueList) {
        super(valueList);
    }

    public CacheDbConnect(String host, int port, String dataBase) {
        this(host, port, dataBase, "", "", "");
    }

    public CacheDbConnect(String host, int port, String dataBase, String userName, String passWord) {
        this(host, port, dataBase, userName, passWord, "");
    }

    public CacheDbConnect(String repliCaset, String dataBase, String userName, String passWord) {
        this(repliCaset, 0, dataBase, userName, passWord, "");
    }

    public CacheDbConnect(String host, int port, String dataBase, String userName, String passWord, String clientCode) {
        this(host, port, dataBase, userName, passWord, "", clientCode);
    }

    public CacheDbConnect(String url) {
        this(url ,"");
    }

    public CacheDbConnect(String url, String clientCode) {
        this("", 0, "", "", "", url, clientCode);
    }

    public CacheDbConnect(String host, int port, String dataBase, String userName, String passWord, String url, String clientCode) {
        super(host, port, dataBase, userName, passWord, url, clientCode);
    }

    public List<String> getRepliCaset() {
        String[] hostArray = host.split(",");
        if(ToolsKit.isNotEmpty(hostArray) && hostArray.length > 1) {
            repliCaset = Arrays.asList(hostArray);
        }
        return repliCaset;
    }

    public void setRepliCaset(List<String> repliCaset) {
        this.repliCaset = repliCaset;
    }


    @Override
    public String getDataSourceFactoryClassName() {
        return null;
    }
}
