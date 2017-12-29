package com.duangframework.rpc.kit;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.utils.RpcUtils;
import com.duangframework.zookeeper.kit.ZooKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date on 2017/12/22.
 */
public class RpcKit {

    private static Logger logger = LoggerFactory.getLogger(RpcKit.class);
    private static RpcKit _rpcKit;
    private static Lock _rpcKitLock = new ReentrantLock();
    private String fieldPath;
    private static Set<String> moduleSet = new HashSet<>();

    public static RpcKit duang() {
        if(null == _rpcKit) {
            try {
                _rpcKitLock.lock();
                _rpcKit = new RpcKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _rpcKitLock.unlock();
            }
        }
        moduleSet.clear();
        return _rpcKit;
    }

    /**
     * 指定接口文件要存放的目录，即接口文件的父目录
     * @param path
     * @return
     */
    public RpcKit fieldPath(String path) {
        fieldPath = path;
        return this;
    }

    /**
     * 要接入的产品代码，即微服务模块名
     * @param productCode
     * @return
     */
    public RpcKit productCode(String productCode) {
        moduleSet.add(productCode);
        return this;
    }

    public void  create() {
        try {
            for (String module : moduleSet) {
                String jsonString = ZooKit.duang().path(RpcUtils.getInterFaceJavaPath(module)).get();
                Map<String,String> map = ToolsKit.jsonParseObject(jsonString, Map.class);
                for(Iterator<Map.Entry<String,String>> iterator =  map.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String,String> entry = iterator.next();
                    String className = entry.getKey();
                    String classSource = entry.getValue();
                    RpcUtils.createInterFaceFileOnDisk(fieldPath, className, classSource);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}
