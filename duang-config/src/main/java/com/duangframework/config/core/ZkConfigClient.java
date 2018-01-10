package com.duangframework.config.core;

import com.duangframework.config.utils.ConfigUtils;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.interfaces.IWatch;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.kit.ZooKit;
import com.duangframework.zookeeper.listener.ZooKeeperListener;

import java.util.*;

/**
 * Created by laotang on 2018/1/8.
 */
public class ZkConfigClient extends AbstractConfig {

    /**
     *  枚举路径地址
     */
    private String enumsFilePath;
    /**
     * ZK父目录地址
     */
    private String zkParentPath;
    /**
     * 需要监听的节点名称，不包括目录
     */
    private Set<String> nodeNames;
    /**
     * 需要监听的目录地址，全地址
     */
    private static Set<String> NODE_PATH_SET = new HashSet<>();

    private ZkConfigClient() {
    }

    public ZkConfigClient(String enumsFilePath, String zkParentPath, Set<String> nodeNames) {
        this.enumsFilePath = enumsFilePath;
        this.zkParentPath = zkParentPath;
        this.nodeNames = nodeNames;
    }

    /**
     * 将配置内容初始化为Map集合
     */
    @Override
    public void initValue2Map() {
        try {
            buildValueMap();
            // ZK监听
            ZooKeeperListener listener = new ZooKeeperListener(new IWatch() {
                @Override
                public void watchChildNote() {
                    buildValueMap();
                }

                @Override
                public void watchDataNote() {
                    buildValueMap();
                }
            }, NODE_PATH_SET);
            // 启动内容监听
            listener.startDataListener();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将ZK上的内容转换为MAP方式缓存到本地
     */
    private void buildValueMap() {
        for(String nodeName : nodeNames) {
            String zkPath = zkParentPath + "/" + nodeName;
            String zkValue = ZooKit.duang().path(zkPath).get();
            Map<String, Object> valueTmpMap = new HashMap<>();
            if (ToolsKit.isNotEmpty(zkValue)) {
                valueTmpMap = ToolsKit.jsonParseObject(zkValue, Map.class);
            }
            if (valueTmpMap.isEmpty()) {
                throw new EmptyNullException("ZkConfigClient builder map is fail: " + zkPath);
            }
            for(Iterator<Map.Entry<String,Object>> it = valueTmpMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String,Object> entry = it.next();
                VALUE_MAP.put(ConfigUtils.createMapKey(nodeName, entry.getKey()), entry.getValue());
            }
            // 创建枚举文件, 不是阿里ECS的
            if("local".equalsIgnoreCase(ToolsKit.getUseEnv())) {
                ConfigUtils.createNumsFile(enumsFilePath, VALUE_MAP);
            }
            NODE_PATH_SET.add(zkValue);
        }
    }

}
