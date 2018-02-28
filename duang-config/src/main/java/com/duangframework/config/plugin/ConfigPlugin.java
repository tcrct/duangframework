package com.duangframework.config.plugin;

import com.duangframework.config.apollo.api.SimpleApolloConfig;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;

/**
 *配置中心插件
 * @author laotang
 * @date 2018/1/8
 */
public class ConfigPlugin implements IPlugin {

    // 客户端类名
    private static SimpleApolloConfig apolloConfig;
    // 环境变量
    private String env;
    // meta地址
    private String metaUrl;

    /**
     * 构造函数
     */
    public ConfigPlugin() {
        if("local".equalsIgnoreCase(ToolsKit.getUseEnv())) {
            env = "dev";
            try {
                metaUrl = PropertiesKit.duang().key("config.meta.url").defaultValue("http://192.168.0.39:8080").asString();
            } catch (Exception e) {
                metaUrl = "http://192.168.0.39:8080";
            }
        } else {
            env = "pro";
            metaUrl = PropertiesKit.duang().key("config.meta.url").defaultValue("https://config.sythealth.com").asString();;
        }
    }

    /**
     * 构造函数
     * @param env  环境变量
     * @param metaUrl  服务器地址
     */
    public ConfigPlugin(String env, String metaUrl) {
        this.env = env;
        this.metaUrl = metaUrl;
    }


    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {
        apolloConfig = new SimpleApolloConfig(env, metaUrl);
    }

    @Override
    public void stop() throws Exception {

    }

    public static SimpleApolloConfig getApolloConfig() {
        return apolloConfig;
    }
}
