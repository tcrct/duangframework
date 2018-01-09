package com.duangframework.config.plugin;

import com.duangframework.config.core.ConfigFactory;
import com.duangframework.config.core.ZkConfigClient;
import com.duangframework.core.common.Const;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;

import java.util.HashSet;
import java.util.Set;

/**
 *配置中心插件
 * @author laotang
 * @date 2018/1/8
 */
public class ConfigPlugin implements IPlugin {

    // 客户端类名
    private String clientClassName;
    // 父目录路径
    private String containerPath;
    // 节点名称集合
    private Set<String> nodeNameSet;
    // 生成枚举文件的全目录地址
    private String enumsFilePath;

    public ConfigPlugin() {
        this(null,null, null, null);
    }

    /**
     * 构造函数
     * @param clientClassName       客户类名
     * @param containerPath             注册中心父目录
     * @param nodeNameSet           节点集合
     */
    public ConfigPlugin(Set<String> nodeNameSet ) {
        this(null,null, null, nodeNameSet);
    }

    /**
     * 构造函数
     * @param clientClassName       客户类名
     * @param enumsFilePath             枚举文件目录地址
     * @param containerPath             注册中心父目录
     * @param nodeNameSet           节点集合
     */
    public ConfigPlugin(String clientClassName, String enumsFilePath, String containerPath, Set<String> nodeNameSet ) {
        this.clientClassName = clientClassName;
        this.enumsFilePath = enumsFilePath;
        this.containerPath = containerPath;
        this.nodeNameSet = nodeNameSet;
    }


    @Override
    public void init() throws Exception {
        if(ToolsKit.isEmpty(clientClassName)) {
            clientClassName = PropertiesKit.duang().key(Const.CONFIG_CLIENTCLASS_NAME).defaultValue(ZkConfigClient.class.getName()).asString();
        }
        if(ToolsKit.isEmpty(containerPath)) {
            containerPath = PropertiesKit.duang().key(Const.CONFIG_CONTAINER_PATH).defaultValue(Const.CONFIG_CONTAINER_PATH_VALUE).asString();
        }
        if(ToolsKit.isEmpty(enumsFilePath)) {
            enumsFilePath = PropertiesKit.duang().key(Const.CONFIG_ENUMS_PATH).asString();
        }
        if(ToolsKit.isEmpty(nodeNameSet)) {
            String[] nodeArray =PropertiesKit.duang().key(Const.CONFIG_NODENAMES).asArray();
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
        // 启动时创建枚举文件
        ConfigFactory.init(clientClassName, enumsFilePath, containerPath, nodeNameSet);
    }

    @Override
    public void stop() throws Exception {

    }
}
