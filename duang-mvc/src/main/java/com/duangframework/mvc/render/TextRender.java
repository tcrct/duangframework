package com.duangframework.mvc.render;

import com.duangframework.core.exceptions.DuangMvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TextRender.
 */
public class TextRender extends Render {

	private static Logger logger = LoggerFactory.getLogger(TextRender.class);
	
	private static final long serialVersionUID = 4775148244778489992L;
	private static final String defaultContentType = "text/plain;charset=" + ENCODING;
	private String text;
	
	public TextRender(String text) {
		this.text = text;
	}
	
	private String contentType;
	public TextRender(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
	}
	
	@Override
	public void render() {
		if(null == request || null == response){
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Response();
		try {
	        if (contentType == null) {
	        	response.setContentType(defaultContentType);
	        }
	        else {
	        	response.setContentType(contentType);
				response.setCharacterEncoding(ENCODING);
	        }
	        response.write(text);
		} catch (Exception e) {
			throw new DuangMvcException(e.getMessage(), e);
		}
	}
}




