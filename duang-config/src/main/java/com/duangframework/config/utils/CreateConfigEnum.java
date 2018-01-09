package com.duangframework.config.utils;

import com.duangframework.core.common.enums.IConfigKeyEnums;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/1/9
 * @since  1.0
 */
public class CreateConfigEnum {

    private static final Logger logger = LoggerFactory.getLogger(CreateConfigEnum.class);

    private static final String CONFIGE_ENUM_NAME = "ConfigKeyEnum";
    private static final String ENUM_PACKAGE = "com.duangframework.config.enums";

    public static void create(String filePath, Set<String> keySet){
        StringBuilder numsString = new StringBuilder();
        String iConfigKeyEnumsPath = IConfigKeyEnums.class.getName();
        numsString.append("package "+ENUM_PACKAGE+";");
        numsString.append("\n/**\n")
                .append("* 自动生成系统配置文件枚举类，如非必要请勿改动\n")
                .append("* @author Created by duangframework\n")
                .append("* @since  1.0\n")
                .append("*/\n")
                .append("public enum "+CONFIGE_ENUM_NAME+" implements  "+iConfigKeyEnumsPath+" {\n\n")
                .append(body(keySet))
                .append("\n}");

        filePath += "/" + ENUM_PACKAGE.replace(".", File.separator)+"/" + CONFIGE_ENUM_NAME + ".java";
        File configEnumsFile = new File(filePath);
        if(configEnumsFile.exists() &&configEnumsFile.isFile()){
            logger.warn(configEnumsFile + " is exists, delete it...");
            configEnumsFile.delete();
        }
        try {
            FileUtils.writeStringToFile(configEnumsFile, numsString.toString());
            logger.warn("create config enums file["+configEnumsFile+"] is success");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private static String body(Set<String> keySet) {
        if(ToolsKit.isEmpty(keySet)){
            return "";
        }
        StringBuilder bodyString = new StringBuilder();
        int size = keySet.size();
        int index = 0;
        for( String key : keySet ) {
            bodyString.append("\t")
                    .append(key.toUpperCase())
                    .append("(\"")
                    .append(key)
                    .append("\")");

            if( index++ < (size-1) ) {
                bodyString.append(",");
            } else {
                bodyString.append(";");
            }
            bodyString.append("\n");

        }

        bodyString.append("\n\n");
        bodyString.append("\tprivate final String value;\n");
        bodyString.append("\tprivate "+CONFIGE_ENUM_NAME+"(String value) { this.value = value; }\n");
        bodyString.append("\t@Override\n");
        bodyString.append("\tpublic String getValue() { return value; }");

        return bodyString.toString();
    }

}
