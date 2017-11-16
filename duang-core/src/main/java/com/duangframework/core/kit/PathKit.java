package com.duangframework.core.kit;

import com.duangframework.core.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 *  路径工具类
 * @author laotang
 * @date 2017/11/16 0016
 */
public class PathKit {

    private static Logger logger = LoggerFactory.getLogger(PathKit.class);

    private static PathKit _pathKit;
    private static Lock _pathKitLock = new ReentrantLock();
    private String resourcePath;

    public static PathKit duang() {
        if(null == _pathKit) {
            try {
                _pathKitLock.lock();
                _pathKit = new PathKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _pathKitLock.unlock();
            }
         }
         return _pathKit;
    }

    public PathKit resource(String resPath) {
        resourcePath = resPath;
        return _pathKit;
    }

    public URL path() {
        return ClassUtils.getClassLoader().getResource(resourcePath.replace(".", "/"));
    }

}
