package com.duangframework.rpc.common;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * 生产者服务信息对象
 *
 */
public class RpcAction implements java.io.Serializable {

	public static final String SERVICE_FIELD = "service";
	public static final String IFACE_FIELD = "iface";
	public static final String INTRANETIP_FIELD = "intranetip";
	public static final String REMOTEIP_FIELD = "remoteip";
	public static final String PORT_FIELD = "port";

	@JSONField(serialize= false, deserialize = false)
	private Class<?> service;// 服务实现类
	@JSONField(serialize= false, deserialize = false)
	private Class<?> iface;// 服务接口类
	private String ifaceName;
	private String intranetip;     // 内网IP地址
	private String remoteip;  // 公网IP地址
	private int port;		// 端口

	public RpcAction(Class<?> service, Class<?> iface, String intranetip, String remoteip, int port) {
		this.service = service;
		this.iface = iface;
		this.ifaceName = iface.getName();
		this.intranetip = intranetip;
		this.remoteip = remoteip;
		this.port = port;
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

	public String getIntranetip() {
		return intranetip;
	}

	public void setIntranetip(String intranetip) {
		this.intranetip = intranetip;
	}

	public String getRemoteip() {
		return remoteip;
	}

	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIfaceName() {
		return ifaceName;
	}

	public void setIfaceName(String ifaceName) {
		this.ifaceName = ifaceName;
	}
}
