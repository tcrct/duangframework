package com.duangframework.rpc.common;

import java.io.Serializable;

/**
 * RPC 返回参数
 */
public class RpcResponse implements Serializable {
	
	private static final long serialVersionUID = 9062588112032194955L;
	private String requestId;		//RPC请求ID
	private Throwable error;
	private long requestStartTime;		//RPC请求开始时间
	private Object result;			//RPC响应结果

	public RpcResponse() {
		super();
	}

	public RpcResponse(long startTime) {
		setRequestStartTime(startTime);
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getRequestStartTime() {
		return requestStartTime;
	}

	public void setRequestStartTime(long requestStartTime) {
		this.requestStartTime = requestStartTime;
	}

	public boolean isError() {
		return error == null ? false : true;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}