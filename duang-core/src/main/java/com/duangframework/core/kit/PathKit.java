package com.duangframework.core.kit;

import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
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
    private String resourcePath = "";

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
        resourcePath = resPath.startsWith("/") ? resPath.substring(1, resPath.length()) : resPath;
        return _pathKit;
    }

    private String rootPath() {
        String rootPath =  path().getPath();
        try {
            return new File(rootPath).getParentFile().getParentFile().getCanonicalPath();
        } catch (IOException e) {
            throw new DuangMvcException(e.getMessage(), e);
        }
    }

    public String web() {
        return rootPath() + File.separator + "WEB-INF" + File.separator + "classes";
    }

    public String lib() {
        return rootPath() + File.separator + "WEB-INF" + File.separator + "lib";
    }

    public URL path() {
        return ClassUtils.getClassLoader().getResource(resourcePath.replace(".", "/"));
    }

    public Enumeration<URL> paths() {
        try {
            return ClassUtils.getClassLoader().getResources(resourcePath.replace(".", "/"));
        } catch (Exception e) {
            throw new DuangMvcException(e.getMessage(), e);
        }
    }

}
