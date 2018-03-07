package com.duangframework.core.config;

import com.duangframework.config.apollo.api.SimpleApolloConfig;
import com.duangframework.config.client.ConfigClient;
import com.duangframework.core.interfaces.IConfig;

/**
 * @author Created by laotang
 * @date createed in 2018/3/7.
 */
public class ConfigFactory {

    private static SimpleApolloConfig apolloConfig = null;
    public static IConfig getConfigClient() {
        IConfig iConfig = null;
        SimpleApolloConfig apolloConfig= ConfigClient.getApolloConfig();
        if(null != apolloConfig) {
            iConfig = new ApolloConfig();
        } else {
            iConfig = new PropertiesConfig();
        }
        return iConfig;
    }

}
