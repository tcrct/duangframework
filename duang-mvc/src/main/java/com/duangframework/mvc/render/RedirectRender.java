//package com.duangframework.mvc.render;
//
//import org.duang.common.exceptios.ServiceException;
//import org.duang.config.InstanceFactory;
//import org.duang.core.Controller;
//import org.duang.kit.ToolsKit;
//
//import java.io.IOException;
//
//
///**
// * 重定向页面
// */
//public class RedirectRender extends Render {
//
//	private static final long serialVersionUID = 1812102713097864255L;
//	private String url;
//	private boolean withQueryString;
//
//	public RedirectRender(String url) {
//		this.url = url;
//	}
//
//	public RedirectRender(String url, boolean withQueryString) {
//		this.url = url;
//		this.withQueryString = withQueryString;
//	}
//
//	public void render() {
//		if(null == request || null == response) return;
//		String contextPath = InstanceFactory.getServletContext().getContextPath();
//		if (contextPath != null && url.indexOf("://") == -1)
//			url = contextPath + url;
//
//		if (withQueryString) {
//			String queryString = request.getQueryString();
//			if (queryString != null)
//				if (url.indexOf("?") == -1)
//					url = url + "?" + queryString;
//				else
//					url = url + "&" + queryString;
//		}
//		String redirectUrl = url;
//		String scheme = ToolsKit.isNotEmpty(request.getHeader(Controller.FORWARDED_PROTO)) ? request.getHeader(Controller.FORWARDED_PROTO) : request.getScheme();
//		if("http".equalsIgnoreCase(scheme)){
//			scheme = ToolsKit.isNotEmpty(request.getHeader(Controller.HTTPS_SCHEME)) ? request.getHeader(Controller.HTTPS_SCHEME) : scheme;
//		}
//		if("https".equalsIgnoreCase(scheme)){
//			String requestUrl = request.getRequestURL().toString().replace(request.getRequestURI().toString(), "");
//			requestUrl += url;
//			if(requestUrl.toLowerCase().startsWith("http")) {
//				redirectUrl = requestUrl.replace("http", scheme);
//			}
//		}
////		System.out.println("##############RedirectRender URL: " + url);
//		try {
//			response.sendRedirect(redirectUrl);
//		} catch (IOException e) {
//			throw new ServiceException(e);
//		}
//	}
//}
//
