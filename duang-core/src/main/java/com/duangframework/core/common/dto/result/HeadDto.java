package com.duangframework.core.common.dto.result;

/**
 *	手机访问后返回的信息头,每一个dto对象须包含
 */
public class HeadDto implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int ret;
	private String msg;
	private String token;
	private String uri;
	private String method;
	private long timestamp = System.currentTimeMillis();
	private String requestId;
	private String clientId;
	
	public HeadDto(){
		
	}
	
	public HeadDto(int ret, String msg){
		this.ret = ret;
		this.msg = msg;
	}
	
	public HeadDto(int ret, String msg, String token){
		this.ret = ret;
		this.msg = msg;
		this.token = token;
	}
	
	public HeadDto(int ret, String msg, String token, String uri){
		this.ret = ret;
		this.msg = msg;
		this.token = token;
		this.uri = uri;
	}
	
	public HeadDto(int ret, String msg, String token, String uri, String method, long timestamp, String requestId, String clientId){
		this.ret = ret;
		this.msg = msg;
		this.token = token;
		this.uri = uri;
		this.method = method;
		this.timestamp = timestamp;
		this.requestId = requestId;
		this.clientId = clientId;
	}
	
	
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
