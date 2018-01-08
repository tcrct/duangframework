package com.duangframework.config.plugin;

import com.duangframework.config.core.ConfigFactory;
import com.duangframework.config.core.ZkConfigClient;
import com.duangframework.config.kit.ConfigKit;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by laotang on 2018/1/8.
 */
public class ConfigPlugin implements IPlugin {

    /**
     * 配置中心客户端类名，全路径
     */
    private static final String CONFIG_CLIENTCLASS_NAME= "config.client.name";
    /**
     * 配置中心容器路径，即注册中心的父目录
     */
    private static final String CONFIG_CONTAINER_PATH= "config.container.path";
    /**
     * 默认的注册中心父目录路径
     */
    private static final String CONFIG_CONTAINER_PATH_VALUE = "";
    /**
     *
     */
    private static final String CONFIG_NODENAMES = "config.node.names";
    // 客户端类名
    private String clientClassName;
    // 父目录路径
    private String containerPath;
    // 节点名称集合
    private Set<String> nodeNameSet;

    public ConfigPlugin() {
    }

    public ConfigPlugin(String clientClassName, String containerPath, Set<String> nodeNameSet ) {
        this.clientClassName = clientClassName;
        this.containerPath = containerPath;
        this.nodeNameSet = nodeNameSet;
    }


    @Override
    public void init() throws Exception {
        if(ToolsKit.isEmpty(clientClassName) && ToolsKit.isEmpty(containerPath) && ToolsKit.isEmpty(nodeNameSet)) {
            clientClassName = PropertiesKit.duang().key(CONFIG_CLIENTCLASS_NAME).defaultValue(ZkConfigClient.class.getName()).asString();
            containerPath = PropertiesKit.duang().key(CONFIG_CONTAINER_PATH).defaultValue(CONFIG_CONTAINER_PATH_VALUE).asString();
            String[] nodeArray =PropertiesKit.duang().key(CONFIG_NODENAMES).asArray();
            if(ToolsKit.isNotEmpty(nodeArray)) {
                nodeNameSet = new HashSet<>(nodeArray.length);
                for(String nodeName : nodeArray) {
                    nodeNameSet.add(nodeName);
                }
            }
        }
    }

    @Override
    public void start() throws Exception {
        ConfigFactory.init(clientClassName, containerPath, nodeNameSet);
    }

    @Override
    public void stop() throws Exception {

    }
}
