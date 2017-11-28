//package com.duangframework.mvc.render;
//
//import org.duang.common.exceptios.ServiceException;
//import org.duang.config.InstanceFactory;
//import org.duang.kit.ToolsKit;
//import org.duang.upload.UploadFile;
//
//import javax.servlet.ServletContext;
//import java.io.*;
//
//
///**
// * FileRender.
// */
//public class FileRender extends Render {
//
//	private static final long serialVersionUID = 4293616220202691369L;
//	private File file;
//	private UploadFile uploadFile;
//
//	public FileRender(UploadFile file) {
//		this.uploadFile = file;
//		this.file = file.getFile();
//	}
//
//	public FileRender(File file) {
//		this.file = file;
//	}
//
//	public void render() {
//		if(null == request || null == response) return;
//		if (file == null || !file.isFile() || file.length() > Integer.MAX_VALUE) {
//			throw new ServiceException("下载的文件不正确");
//        }
//
//		String downLoadName = "";
//
//		if(ToolsKit.isNotEmpty(uploadFile))
//			downLoadName = uploadFile.getOriginalFileName();
//
//		if(ToolsKit.isEmpty(downLoadName)) downLoadName = file.getName();
//
//		try {
//			response.addHeader("Content-disposition", "attachment; filename=" + new String(downLoadName.getBytes("GBK"), "ISO8859-1"));
//		} catch (UnsupportedEncodingException e) {
//			response.addHeader("Content-disposition", "attachment; filename=" + downLoadName);
//		}
//
//		ServletContext servletContext = InstanceFactory.getServletContext();
//
//        String contentType = servletContext.getMimeType(file.getName());
//        if (contentType == null) {
//        	contentType =  "application/octet-stream";
//        }
//
//        response.setContentType(contentType);
//        response.setContentLength((int)file.length());
//        InputStream inputStream = null;
//        OutputStream outputStream = null;
//        try {
//            inputStream = new BufferedInputStream(new FileInputStream(file));
//            outputStream = response.getOutputStream();
//            byte[] buffer = new byte[1024];
//            for (int n = -1; (n = inputStream.read(buffer)) != -1;) {
//                outputStream.write(buffer, 0, n);
//            }
//            outputStream.flush();
//        }
//        catch (Exception e) {
//        	throw new ServiceException(e);
//        }
//        finally {
//            if (inputStream != null) {
//                try {
//					inputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//            }
//            if (outputStream != null) {
//            	try {
//					outputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//            }
//        }
//	}
//}
//
//
