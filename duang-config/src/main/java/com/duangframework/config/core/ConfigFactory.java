package com.duangframework.config.core;


import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.interfaces.IConfig;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;

import java.util.Set;

/**
 * Created by laotang on 2018/1/8.
 */
public class ConfigFactory {

    private static String clientClassName;
    private static IConfig iConfig;

    public static void init(String clsName, String enumsFilePath, String containerPath, Set<String> nodeNameSet) throws Exception{
        clientClassName = clsName;
        Object[] objValue = {enumsFilePath, containerPath, nodeNameSet};
        Class[] objClass = {String.class, String.class, Set.class};
        iConfig =  ClassUtils.newInstance(clientClassName, objValue, objClass);
        iConfig.initValue2Map();
    }

    public static IConfig getClient() {
        if(ToolsKit.isEmpty(iConfig)) {
            throw new EmptyNullException("ConfigFactory getClient is fail: iConfig is null");
        }
        return iConfig;
    }


}
