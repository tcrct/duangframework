package com.duangframework.config.utils;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;

import java.util.Map;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/1/9.
 */
public class ConfigUtils {

    private static final String MAVEN_ITEM_PATH = "/src/main/java";

    /**
     *
     * @param name
     * @param key
     * @return
     */
    public static String createMapKey(String name, String key) {
        return name+"_"+ key;
    }



    /**
     * 根据Key创建枚举文件
     */
    public static void createNumsFile(String filePathTmp, Map<String, Object>  valueMap) {
        final Set<String> keySet = valueMap.keySet();
        if(ToolsKit.isEmpty(keySet)) {
            throw new EmptyNullException("AbstractConfig createNumsFile is fail:  keySet is null");
        }
        //TODO 这里取路径有问题，只能支持Maven格式的目录结构
//        String webPath = PathKit.duang().resource("").path().getPath();
//        String filePathTmp = new File(webPath).getParentFile().getParentFile().getAbsolutePath();
        filePathTmp = filePathTmp.contains(MAVEN_ITEM_PATH) ? filePathTmp.replace(MAVEN_ITEM_PATH, "") : filePathTmp;
        filePathTmp += MAVEN_ITEM_PATH;
        final String filePath = filePathTmp;
        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                CreateConfigEnum.create(filePath, keySet);
            }
        });
    }

}
