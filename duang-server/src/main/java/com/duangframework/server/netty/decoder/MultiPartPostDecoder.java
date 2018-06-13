package com.duangframework.server.netty.decoder;

import com.duangframework.core.common.dto.upload.FileItem;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.*;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class MultiPartPostDecoder extends AbstractDecoder<Map<String,Object>> {

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        // on exit (in normal
        // exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }


    public MultiPartPostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        HttpPostMultipartRequestDecoder requestDecoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                InterfaceHttpData.HttpDataType dataType = httpData.getHttpDataType();
                if(dataType == InterfaceHttpData.HttpDataType.Attribute
                    || dataType == InterfaceHttpData.HttpDataType.InternalAttribute) {
                    setValue2ParamMap(httpData);
                } else if(dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) httpData;
                    long fileLength = 0L;
                    if (null != fileUpload && fileUpload.isCompleted()) {
                        FileItem fileItem = null;
                        byte[] bytes = null;
                        if (fileUpload.isInMemory()) {
                            ByteBuf byteBuf = fileUpload.getByteBuf();
                            if(null == byteBuf) {
                                bytes = ByteBufUtil.getBytes(byteBuf);
                                fileLength = fileUpload.getFile().length();
                            }
                        }

                        if(null == bytes) {
                            try {
                                bytes = Files.readAllBytes(fileUpload.getFile().toPath());
                            } catch (Exception e) {
                                bytes =fileUpload.get();
                            }
                            if(null != bytes) {
                                fileLength = bytes.length;
                            }
                        }
                        if(null == bytes ) {
                            throw new EmptyNullException("upload file is fail: file byte is null" );
                        }
                        fileItem = new FileItem(fileUpload.getName(), fileUpload.getContentTransferEncoding(), fileUpload.getFilename(), fileUpload.getContentType(), fileLength, bytes);
                        attributeMap.put(fileItem.getName(), fileItem);
                    }
                }
            }
        }
        return attributeMap;
    }

//    private String getUploadFileName(InterfaceHttpData data) {
//        String content = data.toString();
//        String temp = content.substring(0, content.indexOf("\n"));
//        content = temp.substring(temp.lastIndexOf("=") + 2, temp.lastIndexOf("\""));
//        return content;
//    }
//    private String getUploadFileExtName(String fileName) {
//        if(!fileName.contains(".")) {
//            throw new IllegalArgumentException("文件扩展名不存在");
//        }
//        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
//    }


    private void setValue2ParamMap(InterfaceHttpData httpData) throws Exception {
        MixedAttribute attribute = (MixedAttribute) httpData;
        String key = attribute.getName();
        String value = attribute.getValue();
        if(ToolsKit.isNotEmpty(value)) {
            attributeMap.put(key, value);
        }
    }
}
