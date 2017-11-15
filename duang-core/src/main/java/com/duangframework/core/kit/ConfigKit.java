package com.duangframework.core.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 解释duang.json文件
 * 用作整个系统的配置
 * @author laotang
 * @date 2017/11/16 0016
 */
public class ConfigKit {

    private static Logger logger = LoggerFactory.getLogger(ConfigKit.class);

    private static ConfigKit _configKit;
    private static Lock _configKitLock = new ReentrantLock();
    private String resourcePath;

    public static ConfigKit duang() {
        if(null == _configKit) {
            try {
                _configKitLock.lock();
                _configKit = new ConfigKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _configKitLock.unlock();
            }
        }
        return _configKit;
    }

}
