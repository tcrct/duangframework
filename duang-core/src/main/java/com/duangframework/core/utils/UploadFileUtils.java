package com.duangframework.core.utils;

import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.dto.upload.UploadFileHandle;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.PathKit;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class UploadFileUtils {

    private static Logger logger = LoggerFactory.getLogger(UploadFileHandle.class);
    private static final String PRODUCT_CODE = "product.code";
    private static final String UPLOADFILE_DIRECTORY = "uploadfile.directory";
    /**
     * 创建上传文件存放在服务器的绝对路径，包含文件名
     * @return
     */
    public static String builderServerFileDir(String saveDir) {


        String rootDir = PathKit.duang().resource("/").path().getPath();
        String uploadfilesDir = ConfigKit.duang().key(UPLOADFILE_DIRECTORY).defaultValue("uploadfiles").asString();
        if(ToolsKit.isEmpty(saveDir)) {
            String productCode = ConfigKit.duang().key(PRODUCT_CODE).defaultValue("duangframework").asString();
            String currentDate = ToolsKit.formatDate(new Date(), "yyyyMMdd");
            saveDir = productCode+"/"+currentDate;
        }

        saveDir = checkDirString(saveDir);

        String path =  rootDir+"/" +uploadfilesDir+"/" +saveDir;
        path = checkDirString(path);
        if(path.endsWith("classes")) {
            path = path.substring(0, path.length() - 7);
        }
        logger.debug("upload file on server path : "  +  path);
        return path;
    }

    public static String builderServerFileName(String fileName, boolean isUUIDName) {
        if(isUUIDName) {
            fileName = new DuangId().toString() +"."+getUploadFileExtName(fileName);
        }
        logger.debug("upload file on server name : "  +  fileName);
        return fileName;
    }


    private static String getUploadFileExtName(String fileName) {
        if(!fileName.contains(".")) {
            throw new IllegalArgumentException("文件扩展名不存在");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    }

    private static String checkDirString(String dir) {
        dir = dir.startsWith("/") ? dir.substring(1, dir.length()) : dir;
        dir = dir.endsWith("/") ? dir.substring(0, dir.length()-1) : dir;
        return dir.trim();
    }
}
