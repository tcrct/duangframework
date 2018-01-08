package com.duangframework.config.core;

import com.alibaba.fastjson.JSONArray;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.interfaces.IConfig;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.kit.ZooKit;

import java.util.*;

/**
 * Created by laotang on 2018/1/8.
 */
public class ZkConfigClient extends AbstractConfig {

    private String zkParentPath;
    private Set<String> nodeNames;

    private ZkConfigClient() {
    }

    public ZkConfigClient(String zkParentPath, Set<String> nodeNames) {
        this.zkParentPath = zkParentPath;
        this.nodeNames = nodeNames;
        init();
    }

    /**
     * 将配置内容初始化为Map集合
     */
    @Override
    public void init() {
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
                    String key = nodeName +"_" + entry.getKey();
                    VALUE_MAP.put(key, entry.getValue());
                }
                // 创建枚举文件
                createNumsFile();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
