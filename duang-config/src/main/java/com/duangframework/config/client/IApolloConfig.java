package com.duangframework.config.client;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/3/6.
 */
public interface IApolloConfig {
    /**
     * 应用APPID
     * @return
     */
    String getAppId();

    /**
     * Apollo NameSpaces集合
     * @return
     */
    List<String> getNameSpaces();

    /**
     * 环境字段
     * <p>dev:  内测环境</p>
     * <p>uat:  公测环境</p>
     * <p>pro:  生产环境</p>
     */
    String getEnv();

    /**
     *  Apollo Config Server 完整URL
     */
    String getMetaUrl();
}
