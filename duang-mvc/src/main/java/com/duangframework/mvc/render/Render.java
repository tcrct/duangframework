package com.duangframework.mvc.render;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.http.head.HttpHeaders;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.kit.ConfigKit;

import java.io.Serializable;


public abstract class Render implements Serializable {
	
	private static final long serialVersionUID = -8406693915721288408L;
	protected static final String _DUANG_BACKDOOR_PWD_  = "laotang";
	protected  static final String ENCODING = ConfigKit.duang().key("encoding").defaultValue("UTF-8").asString();
	protected IRequest request;
	protected IResponse response;
	protected Object obj;
	protected String view;

	
	public final Render setContext(IRequest request, IResponse response) {
		this.request = request;
		this.response = response;
		return this;
	}
	
	public final Render setContext(IRequest request, IResponse response, String view) {
		this.request = request;
		this.response = response;
		this.view = view;
		return this;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
	
	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	protected void setDefaultValue2Response() {
		response.setHeader(HttpHeaders.PRAGMA, "no-cache");
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		response.setHeader(HttpHeaders.EXPIRES, "0");
		response.setHeader(Const.OWNER_FILED, Const.OWNER);
		response.setHeader(Const.STATUS, "200");
	}

	public abstract void render();
}
