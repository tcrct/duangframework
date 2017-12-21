package com.duangframework.rpc.common;

import java.io.Serializable;


/**
 * RPC 请求参数
 */
public class RpcRequest implements Serializable {

	private static final long serialVersionUID = -707895616805221772L;
	private String requestId; // 流水ID
	private String iface; // 接口
	private String service; // 实现类
	private String methodName; // 方法名
	private Class<?> methodResult;// 返回类型
	private Class<?>[] parameterTypes;// 入参类型
	private Object[] parameters;// 入参对象
	private String version;// 版本号
	private boolean haveNullParams = false;
	private long timeout = 3000; //返回超时时间(毫秒)
	private long startTime;		//RPC请求开始时间


	public RpcRequest() {
		super();
	}

	public RpcRequest(long startTime, String requestId) {
		setStartTime(startTime);
		setRequestId(requestId);
	}

	public RpcRequest(String requestId, String iface, String service, String methodName, Class<?> methodResult,
					  Class<?>[] parameterTypes, Object[] parameters, String version, boolean haveNullParams, long timeout) {
		super();
		this.requestId = requestId;
		this.iface = iface;
		this.service = service;
		this.methodName = methodName;
		this.methodResult = methodResult;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
		this.version = version;
		this.haveNullParams = haveNullParams;
		this.timeout = timeout;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getIface() {
		return iface;
	}

	public void setIface(String iface) {
		this.iface = iface;
	}
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?> getMethodResult() {
		return methodResult;
	}

	public void setMethodResult(Class<?> methodResult) {
		this.methodResult = methodResult;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean getHaveNullParams() {
		return haveNullParams;
	}

	public void setHaveNullParams(boolean haveNullParams) {
		this.haveNullParams = haveNullParams;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}