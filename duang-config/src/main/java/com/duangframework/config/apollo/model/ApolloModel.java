package com.duangframework.config.apollo.model;

import java.util.List;

/**
 *
 * @author Created by laotang
 * @date createed in 2018/3/1.
 */
public class ApolloModel implements java.io.Serializable {

    /**
     *  应用APPID
     */
    private String appId;
    /**
     *  Apollo NameSpaces集合
     */
    private List<String> nameSpaces;
    /**
     * 环境字段
     * <p>dev:  内测环境</p>
     * <p>uat:  公测环境</p>
     * <p>pro:  生产环境</p>
     */
    private String env;
    /**
     *  Apollo Config Server 完整URL
     */
    private String metaUrl;

    public ApolloModel() {
    }

    public ApolloModel(String appId, List<String> nameSpaces, String env, String metaUrl) {
        this.appId = appId;
        this.nameSpaces = nameSpaces;
        this.env = env;
        this.metaUrl = metaUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getNameSpaces() {
        return nameSpaces;
    }

    public void setNameSpaces(List<String> nameSpaces) {
        this.nameSpaces = nameSpaces;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getMetaUrl() {
        return metaUrl;
    }

    public void setMetaUrl(String metaUrl) {
        this.metaUrl = metaUrl;
    }

    @Override
    public String toString() {
        return "ApolloModel{" +
                "appId='" + appId + '\'' +
                ", nameSpaces=" + nameSpaces +
                ", env='" + env + '\'' +
                ", metaUrl='" + metaUrl + '\'' +
                '}';
    }
}
