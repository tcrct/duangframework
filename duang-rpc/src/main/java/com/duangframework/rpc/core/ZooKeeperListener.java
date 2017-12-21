package com.duangframework.rpc.core;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.core.ZkChildListener;
import com.duangframework.zookeeper.kit.ZooKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * 如果监听到内容或目录节点发生变化，则将actionMap清空，再重新调用ZK去拿具体的信息
 * Created by laotang on 2017/4/11.
 */
public class ZooKeeperListener {

    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperListener.class);

    private volatile  Set<String> childernZkPathList;

    public ZooKeeperListener(Set<String> childernZkPathList) {
        this.childernZkPathList = childernZkPathList;
    }

    public void startListener() {
        try {
            if(ToolsKit.isEmpty(childernZkPathList)){
                return;
            }
            for(String childernPath : childernZkPathList) {
                //节点内容监听
                ZooKit.duang().path(childernPath).listener(new ZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        logger.warn("[" + parentPath + "] ZooKeeper Listener handleChildChange");
                        RpcFactory.watchNode();
                    }
                });
                logger.warn("zookeeper node [" + childernPath + "] listener is run...");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
