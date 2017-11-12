package com.duangframework.mvc.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2017/11/11 0011.
 */
public class ClassKit {

    private static Logger logger = LoggerFactory.getLogger(ClassKit.class);

    private static ClassKit _classKit;
    private static Lock _lock = new ReentrantLock();
    private Map<String, Class<?>> annotationMap = new HashMap<>();
    private Map<String, String> packageMap = new HashMap<>();
    private Map<String, String> jarNameMap = new HashMap<>();
    private Set<String> suffixSet  = new HashSet<>();

    public static ClassKit duang() {
        if(null == _classKit) {
            try {
                _lock.lock();
                _classKit = new ClassKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _lock.unlock();
            }
        }
        return _classKit;
    }

    /**
     * 添加要扫描的类文件包含的注解class, 若有多个时，重复调用
     * @return
     */
    public ClassKit annotation() {
        return _classKit;
    }

    /**
     * 添加要扫描类文件的包路径，若有多个时，重复调用
     * @return
     */
    public ClassKit packages() {
        return _classKit;
    }

    /**
     * 添加要扫描类文件的所在的jar文件名，若有多个时，重复调用
     * @return
     */
    public ClassKit jarname() {
        return _classKit;
    }

    /**
     * 添加要扫描类文件的后缀标识，若有多个时，重复调用
     * @return
     */
    public ClassKit suffix() {
        return _classKit;
    }

    public List<Class<?>> list() {

        return null;
    }

}
