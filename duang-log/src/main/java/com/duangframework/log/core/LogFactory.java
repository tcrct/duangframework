package com.duangframework.log.core;

import com.duangframework.log.sdk.aliyun.SLSLogClient;
import com.duangframework.log.sdk.qiniu.QiniuLogClient;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogFactory {

    public static ILog getLogClient(LogEnum logEnum) {
        ILog logClient = null;
        // 阿里云
        if (LogEnum.ALIYUN.equals(logEnum)) {
            logClient = SLSLogClient.getInstance();
        }
        // 七牛
        if (LogEnum.QINIU.equals(logEnum)) {
            logClient = QiniuLogClient.getInstance();
        }
        return logClient;
    }

}
