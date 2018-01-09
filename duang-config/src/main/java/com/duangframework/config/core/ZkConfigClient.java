package com.duangframework.config.core;

import com.duangframework.config.utils.ConfigUtils;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.kit.ZooKit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by laotang on 2018/1/8.
 */
public class ZkConfigClient extends AbstractConfig {

    private String enumsFilePath;
    private String zkParentPath;
    private Set<String> nodeNames;

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
                // 创建枚举文件
                ConfigUtils.createNumsFile(enumsFilePath, VALUE_MAP);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
