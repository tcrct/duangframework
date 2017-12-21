package com.duangframework.rpc.common;


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

	private String service;// 服务实现类
	private String iface;// 服务接口类
	private String intranetip;     // 内网IP地址
	private String remoteip;  // 公网IP地址
	private int port;		// 端口

	public RpcAction(String service, String iface, String intranetip, String remoteip, int port) {
		this.service = service;
		this.iface = iface;
		this.intranetip = intranetip;
		this.remoteip = remoteip;
		this.port = port;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getIface() {
		return iface;
	}

	public void setIface(String iface) {
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
}
