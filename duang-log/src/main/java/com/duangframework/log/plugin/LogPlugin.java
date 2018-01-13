package com.duangframework.log.plugin;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.log.core.LogEnum;
import com.duangframework.log.sdk.aliyun.LoghubFactory;
import com.duangframework.log.utils.LogUtils;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogPlugin implements IPlugin {

    public LogPlugin() {
        this(null);
    }

    public LogPlugin(LogEnum enums) {
        if(ToolsKit.isEmpty(enums)) {
            enums = LogEnum.ALIYUN;
        }
        LogUtils.setLogEnum(enums);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() throws Exception {
        // 阿里云SLS
        if(LogEnum.ALIYUN.equals(LogUtils.getLogEnum())) {
            LoghubFactory.getInstance();
        }
    }

    @Override
    public void stop() throws Exception {

    }
}
