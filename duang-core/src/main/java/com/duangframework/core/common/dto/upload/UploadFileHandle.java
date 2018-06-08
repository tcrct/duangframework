package com.duangframework.core.common.dto.upload;

import com.duangframework.core.common.DuangId;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.PathKit;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class UploadFileHandle {

    private static Logger logger = LoggerFactory.getLogger(UploadFileHandle.class);
    private static final String PRODUCT_CODE = "product.code";
    private static final String UPLOADFILE_DIRECTORY = "uploadfile.directory";

    private FileItem fileItem;      //上传到netty后，封装成FileItem对象
    private String saveDir; // 上传文件存放到服务器的目录 ，绝对路径
    private boolean isUUIDName; // 是否要生成新的文件名

    public UploadFileHandle(FileItem fileItem, String saveDir, boolean isUUIDName) {
        this.fileItem = fileItem;
        this.saveDir =saveDir;
        this.isUUIDName = isUUIDName;
    }

    public UploadFile getUploadFile() {
        String serverFileDir = builderServerFileDir();
        String serverFileName = builderServerFileName();
        File dir = new File(serverFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(serverFileDir, serverFileName);
        UploadFile uploadFile = null;
        try {
            FileUtils.writeByteArrayToFile(dest, fileItem.getData());
            uploadFile = new UploadFile(fileItem.getName(),
                    serverFileDir,
                    serverFileName,
                    fileItem.getFileName(),
                    fileItem.getName(),
                    fileItem.getContentType(),
                    fileItem.getLength());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return uploadFile;
    }

    /**
     * 创建上传文件存放在服务器的绝对路径，包含文件名
     * @return
     */
    private String builderServerFileDir() {
        if(ToolsKit.isEmpty(fileItem)) {
            throw new EmptyNullException("fileitem is null");
        }

        String rootDir = PathKit.duang().resource("/").path().getPath();
        String uploadfilesDir = ConfigKit.duang().key(UPLOADFILE_DIRECTORY).defaultValue("uploadfiles").asString();
        if(ToolsKit.isEmpty(saveDir)) {
            String productCode = ConfigKit.duang().key(PRODUCT_CODE).defaultValue("duangframework").asString();
            String currentDate = ToolsKit.formatDate(new Date(), "yyyyMMdd");
            saveDir = productCode+"/"+currentDate;
        }

        if(saveDir.startsWith("/")) {
            saveDir = saveDir.substring(1, saveDir.length());
        }
        if(saveDir.endsWith("/")) {
            saveDir = saveDir.substring(0, saveDir.length()-1);
        }

        String path =  rootDir+"/" +uploadfilesDir+"/" +saveDir;
        logger.debug("upload file on server path : "  +  path);
        return path;
    }

    private String builderServerFileName() {
        String fileName = fileItem.getFileName();
        if(isUUIDName) {
            fileName = new DuangId().toString() +"."+getUploadFileExtName(fileName);
        }
        logger.debug("upload file on server name : "  +  fileName);
        return fileName;
    }


    private String getUploadFileExtName(String fileName) {
        if(!fileName.contains(".")) {
            throw new IllegalArgumentException("文件扩展名不存在");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    }
}
