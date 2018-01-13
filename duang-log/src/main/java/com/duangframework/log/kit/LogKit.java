package com.duangframework.log.kit;

import com.duangframework.log.core.LogDto;
import com.duangframework.log.core.LogEnum;
import com.duangframework.log.core.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志工具类
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogKit {

    private static Logger logger = LoggerFactory.getLogger(LogKit.class);

    private static LogKit _logKit;
    private static Lock _logKitLock = new ReentrantLock();
    private static LogDto logDto;
    private static LogEnum logEnum;

    public static LogKit duang() {
        if(null == _logKit) {
            try {
                _logKitLock.lock();
                _logKit = new LogKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _logKitLock.unlock();
            }
        }
        clear();
        return _logKit;
    }

    private static void clear() {
        logDto = null;
        logEnum = null;
    }

    public LogKit param(LogDto requestDto) {
        logDto = requestDto;
        return this;
    }

    public LogKit channel(LogEnum logEnum) {
        LogKit.logEnum = logEnum;
        return this;
    }

    public void submit() {
        try {
            LogFactory.getLogClient(logEnum).submit(logDto);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}
