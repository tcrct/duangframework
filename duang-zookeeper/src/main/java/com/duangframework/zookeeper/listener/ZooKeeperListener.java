package com.duangframework.zookeeper.listener;

import com.duangframework.core.interfaces.IWatch;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.core.ZkChildListener;
import com.duangframework.zookeeper.core.ZkDataListener;
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

    private volatile  Set<String> zkPathList;

    private IWatch watch;

    /**
     * 构造方法
     * @param watch             监听实现类接口
     * @param zkPathList       监听的目录或文件全路径
     */
    public ZooKeeperListener(IWatch watch, Set<String> zkPathList) {
        this.watch = watch;
        this.zkPathList = zkPathList;
    }

    /**
     *  启动目录监听
     */
    public void startChildListener() {
        try {
            if(ToolsKit.isEmpty(zkPathList)){
                return;
            }
            for(String childernPath : zkPathList) {
                //目录监听
                ZooKit.duang().path(childernPath).listener(new ZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        logger.warn("[" + parentPath + "] ZooKeeper Listener handleChildChange");
                        watch.watchChildNote();
                    }
                });
                logger.warn("zookeeper child node [" + childernPath + "] listener is run...");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     *  启动内容节点监听
     */
    public void startDataListener() {
        try {
            if(ToolsKit.isEmpty(zkPathList)){
                return;
            }
            for(String dataPath : zkPathList) {
                //内容节点监听
                ZooKit.duang().path(dataPath).listener(new ZkDataListener() {
                    @Override
                    public void handleDataChange(String dataPath, Object data) throws Exception {
                        logger.warn("[" + dataPath + "] ZooKeeper Listener handleDataChange");
                        watch.watchDataNote();
                    }

                    @Override
                    public void handleDataDeleted(String dataPath) throws Exception {
                        logger.warn("[" + dataPath + "] ZooKeeper Listener handleDataDeleted");
                        watch.watchDataNote();
                    }
                });
                logger.warn("zookeeper data node [" + dataPath + "] listener is run...");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
