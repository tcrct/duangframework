package com.duangframework.mvc.render;


import com.duangframework.core.common.dto.upload.DownLoadStream;
import com.duangframework.core.common.dto.upload.UploadFile;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.utils.UploadFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;


/**
 * FileRender.
 */
public class FileRender extends Render {
	
	private static final long serialVersionUID = 4293616220202691369L;
	private File file;
	private UploadFile uploadFile;
	private DownLoadStream stream;

	public FileRender(UploadFile file) {
		this.uploadFile = file;
		this.file = file.getFile();
	}


	public FileRender(File file) {
		this.file = file;
	}

    public FileRender(DownLoadStream stream) {
        this.stream = stream;
        byte[] bytes = new byte[8192];
        try {
            IOUtils.write(bytes, stream.getOutputStream());
            String serverFileDir = UploadFileUtils.builderServerFileDir("");
            String serverFileName = UploadFileUtils.builderServerFileName(stream.getFileName(), false);
            File dir = new File(serverFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(serverFileDir, serverFileName);
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void render() {
		if(null == request || null == response) {
			return;
		}
		try {
			if (file == null || !file.isFile() || file.length() > Integer.MAX_VALUE) {
				throw new ServiceException("下载的文件不正确");
			}
//			String downLoadName = file.getName();
//			try {
//				response.addHeader("Content-disposition", "attachment; filename=" + new String(downLoadName.getBytes("GBK"), "ISO8859-1"));
//			} catch (UnsupportedEncodingException e) {
//				response.addHeader("Content-disposition", "attachment; filename=" + downLoadName);
//			}
//			response.setContentType(ContentTypeEnums.get(downLoadName));
//			response.setContentLength((int)file.length());
			response.write(file);
		} catch (Exception e) {
			throw new DuangMvcException(e.getMessage(), e);
		}
	}
}


