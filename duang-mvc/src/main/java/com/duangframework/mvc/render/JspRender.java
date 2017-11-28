//package com.duangframework.mvc.render;
//
//import org.duang.config.Config;
//import org.duang.config.InstanceFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletException;
//import java.io.IOException;
//
//public class JspRender extends Render {
//
//	private static Logger logger = LoggerFactory.getLogger(JspRender.class);
//
//	private static final long serialVersionUID = -7170413556280877767L;
//
//	public JspRender(String view, String extname) {
//		this.view = (InstanceFactory.getServletContext().getContextPath() + Config.getViewRootPath() + view + extname).trim();
//	}
//
//	@Override
//	public void render() {
//		if(null == request || null == response) return;
//		try {
//			logger.debug("JspRender: " + view);
////			request.getRequestDispatcher(view).forward(request, response);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
