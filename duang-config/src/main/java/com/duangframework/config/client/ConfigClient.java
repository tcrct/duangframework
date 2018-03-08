package com.duangframework.config.client;

import com.duangframework.config.apollo.api.SimpleApolloConfig;
import com.duangframework.config.apollo.model.ApolloModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *配置中心插件
 * @author laotang
 * @date 2018/1/8
 */
public class ConfigClient {

    private static Logger logger = LoggerFactory.getLogger(ConfigClient.class);

    // 客户端类名
    private volatile static SimpleApolloConfig apolloConfig;
    private ApolloModel model;

    /**
     * 构造函数
     * @ model  ApolloModel对象实例
     */
    public ConfigClient(IApolloConfig model) {
        this(model.getAppId(), model.getNameSpaces(), model.getEnv(), model.getMetaUrl());
    }

    /**
     * 构造函数
     * @param appid  配置中心应用ID
     * @param nameSpaceList  命名空间集合
     * @param env  环境变量
     * @param metaUrl  Apollo Config服务器地址
     */
    public ConfigClient(String appid, List<String> nameSpaceList, String env, String metaUrl) {
        if(null == appid || appid.length() ==0) {
            throw new NullPointerException("配置中心应用ID不能为空!");
        }
        if(null == env || env.length() ==0) {
            throw new NullPointerException("环境变量不能为空!");
        }
        if(null == metaUrl || metaUrl.length() ==0) {
            throw new NullPointerException("Apollo Config服务器地址不能为空!");
        }
        String commonNameSpace = "SG.common";
        if(nameSpaceList.isEmpty()) { nameSpaceList = new ArrayList<>(); }
        if(!nameSpaceList.contains(commonNameSpace)) {
            nameSpaceList.add(commonNameSpace);
        }
        if(!metaUrl.startsWith("http")) {
            throw new IllegalArgumentException("Apollo Config服务器地址必须是http协议");
        }
        model = new ApolloModel(appid, nameSpaceList, env, metaUrl);
    }

    public void start() throws Exception {
        apolloConfig = new SimpleApolloConfig(model);
        logger.warn("ConfigClient start success...");
    }

    public static SimpleApolloConfig getApolloConfig() {
        return apolloConfig;
    }
}
