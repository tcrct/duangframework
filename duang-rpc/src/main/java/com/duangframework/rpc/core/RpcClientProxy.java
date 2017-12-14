package com.duangframework.rpc.core;

import com.duangframework.core.common.aop.ProxyChain;
import com.duangframework.core.interfaces.IProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者Rpc代理类
 */
public class RpcClientProxy implements IProxy {

	private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

	@Override
	public Object doProxy(ProxyChain proxyChain) throws Exception {
//		Method method = proxyChain.getTargetMethod();
//		Class<?> targetClass =  proxyChain.getTargetClass();
//		// 发现服务， 第一次调用时才触发去调用ZK去拿具体的信息
//		RpcAction action = RpcFactory.discoverService(targetClass);
//		if(ToolsKit.isEmpty(action)) {
//			throw new RpcException("Can't Discover " + targetClass.getName() +" RpcAction! Please Check ZooKeep Server");
//		}
//		RpcRequest request = new RpcRequest(System.currentTimeMillis()); // 创建并初始化 RPC 请求
//		request.setRequestId(RpcUtils.getRequestId());
//		request.setIface(targetClass.getName());
//		request.setMethodName(method.getName());
//		request.setParameterTypes(method.getParameterTypes());
//		request.setParameters(proxyChain.getMethodParams());
//		request.setService(action.getService().getName());
//		logger.warn("["+request.getStartTime() + "] request["+request.getRequestId()+"] ["+action.getRemoteip()+ "/" + action.getIntranetip()+":"+action.getPort()+"] "+ request.getIface()+"."+request.getMethodName());
//		RpcResponse response = RpcClient.getInstance().call(request, action);
//		if (response != null ) {
//			if(response.isError()) {
//				logger.warn("response error:" + response.getError().getMessage());
//				throw new RpcException(response.getError());
//			} else {
//				return response.getResult();
//			}
//		}
		return null;
	}

}
