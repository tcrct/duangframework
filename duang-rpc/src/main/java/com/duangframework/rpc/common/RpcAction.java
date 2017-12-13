package com.duangframework.rpc.common;


import com.duangframework.core.annotation.rpc.Rpc;

/**
 * 生产者服务信息对象
 *
 */
public class RpcAction implements java.io.Serializable {

	public static final String SERVICE_FIELD = "service";
	public static final String IFACE_FIELD = "iface";
	public static final String ANNONT_FIELD = "rpcAnnontation";

	private Class<?> service;// 服务实现类
	private Class<?> iface;// 服务接口类
	private Rpc rpcAnnontation;		// 服务注解

	public RpcAction(Class<?> service, Class<?> iface, Rpc rpcAnnontation) {
		this.service = service;
		this.iface = iface;
		this.rpcAnnontation = rpcAnnontation;
	}

	public Class<?> getService() {
		return service;
	}

	public void setService(Class<?> service) {
		this.service = service;
	}

	public Class<?> getIface() {
		return iface;
	}

	public void setIface(Class<?> iface) {
		this.iface = iface;
	}

	public Rpc getRpcAnnontation() {
		return rpcAnnontation;
	}

	public void setRpcAnnontation(Rpc rpcAnnontation) {
		this.rpcAnnontation = rpcAnnontation;
	}
}
