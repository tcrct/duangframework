package com.duangframework.core.common.enums;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeEnums {
	
	    private static final Map<String, String> contentTypeMap = new HashMap<String, String>();
//		private static final List<String> notBackupList = new ArrayList<>();
	static {
	    	contentTypeMap.put("jpeg", "image/jpeg");
	        contentTypeMap.put("jpg", "image/jpeg");
	        contentTypeMap.put("gif", "image/gif");
	        contentTypeMap.put("png", "image/png");
	        contentTypeMap.put("html", "text/html");
	        contentTypeMap.put("htm", "text/html");
	        contentTypeMap.put("css", "text/css");
	        contentTypeMap.put("js", "text/javascript");
	        contentTypeMap.put("otf","application/x-font-otf");
	        contentTypeMap.put("eot","application/vnd.ms-fontobject");
	        contentTypeMap.put("svg","image/svg+xml");
	        contentTypeMap.put("ttf","application/x-font-ttf");
	        contentTypeMap.put("woff","application/x-font-woff");
	        contentTypeMap.put("map","application/octet-stream");
	        contentTypeMap.put("pdf","application/pdf");
	        contentTypeMap.put("zip","application/zip");
	        contentTypeMap.put("xml","text/xml");
	        contentTypeMap.put("xls","application/x-excel");
	        contentTypeMap.put("xlsx","application/x-excel");
	        contentTypeMap.put("doc","application/msword");
	        contentTypeMap.put("docx","application/msword");
	        contentTypeMap.put("txt","text/plain");
	        contentTypeMap.put("tif","image/tiff");
	        contentTypeMap.put("tgz","application/x-compressed");
	        contentTypeMap.put("gz","application/x-gzip");
	        contentTypeMap.put("tar","application/x-tar");
	        contentTypeMap.put("swf","application/x-shockwave-flash");
	        contentTypeMap.put("shtml","text/html");
	        contentTypeMap.put("sh","application/x-sh");
	        contentTypeMap.put("py","text/x-script.phyton");
	        contentTypeMap.put("mpeg","video/mpeg");
	        contentTypeMap.put("mp3","audio/mpeg3");
	        contentTypeMap.put("mov","video/quicktime");
	        contentTypeMap.put("mime","www/mime");
	        contentTypeMap.put("py","text/x-script.phyton");
	        contentTypeMap.put("java","text/plain");
	        contentTypeMap.put("ico","image/x-icon");
	        contentTypeMap.put("cpp","text/x-c");	        
	        contentTypeMap.put("class","application/java");
	        contentTypeMap.put("c","text/plain");
	        contentTypeMap.put("bmp","image/bmp");
	        contentTypeMap.put("avi","video/avi");

		    // 压缩文件与视频流文件不备份
//			notBackupList.add("zip");
//			notBackupList.add("tar");
//			notBackupList.add("tgz");
//			notBackupList.add("gz");
//			notBackupList.add("swf");
//			notBackupList.add("mp3");
//			notBackupList.add("avi");
//			notBackupList.add("apk");
//			notBackupList.add("jar");
//			notBackupList.add("mp4");
	    };


	public static String get(String fileName){
		String result = null;
		if(fileName.lastIndexOf(".") > -1) {
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			result = contentTypeMap.get(extName.trim().toLowerCase());
		}
		return result != null ? result : "application/octet-stream";
	}

		public static boolean isBackupFile(String extName){
	    	return true;		//全备份
//			return !notBackupList.contains(extName);
		}
	    
	    public static void add(Map<String, String> valueMap) {
	    	contentTypeMap.putAll(valueMap);
	    }
	}





