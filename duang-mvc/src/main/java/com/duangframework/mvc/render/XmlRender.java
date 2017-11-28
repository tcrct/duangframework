package com.duangframework.mvc.render;


import com.duangframework.core.exceptions.DuangMvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * XmlRender.
 */
public class XmlRender extends Render {
	
	private static final long serialVersionUID = 4775148244778489992L;
	private static Logger logger = LoggerFactory.getLogger(XmlRender.class);
	private static final String defaultContentType = "text/xml;charset=" +ENCODING;
	private String text;
	
	public XmlRender(String text) {
		this.text = text;
	}
	
	private String contentType;
	public XmlRender(String text, String contentType) {
		this.text = text;
		this.contentType = contentType;
	}
	
	public void render() {
		if(null == request || null == response) {
			logger.warn("request or response is null");
			return;
		}
		setDefaultValue2Obj();
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




