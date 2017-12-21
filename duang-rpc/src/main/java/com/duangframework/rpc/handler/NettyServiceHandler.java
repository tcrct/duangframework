package com.duangframework.rpc.handler;


import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.rpc.common.*;
import com.duangframework.rpc.core.RpcFactory;
import com.duangframework.rpc.utils.RpcUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * duang-rpc  server netty 处理器
 * 接收客户端发送过来的请求
 *
 * @author laotang*/
public class NettyServiceHandler extends SimpleChannelInboundHandler<MessageHolder<RpcRequest>> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServiceHandler.class);

	public NettyServiceHandler() {
	}

	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final MessageHolder<RpcRequest> holder) throws Exception {
		RpcRequest request = holder.getBody();
		if(ToolsKit.isEmpty(request)) {
			throw new EmptyNullException("request is null");
		}
		RpcResponse response =new RpcResponse(request.getStartTime(), request.getRequestId());
		try {
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable t) {
			response.setError(t);
		}
		// 根据netty上下方，取出通道，如果通道正常，返回将结果以异步的方式写入，返回到请求端
		if(ctx.channel().isOpen()){
			try{
				MessageHolder<RpcResponse> messageHolder = new MessageHolder<RpcResponse>(Protocol.RESPONSE, Protocol.OK,response);
				ChannelFuture wf = ctx.channel().writeAndFlush(messageHolder).sync();
				wf.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							ctx.channel().close();
						}
					}
				});
			}catch(Exception e) {
				logger.warn(e.getMessage(), e);
			}
		} else {
			logger.warn("NettyServiceHandler : channel is close!");
		}
	}

	private Object handle(RpcRequest request) throws Throwable {
		RpcAction rpcAction = RpcFactory.getRpcActionMap().get(request.getIface());
		Class<?> ifaceClass = rpcAction.getIface();
		Class<?> serviceClass = rpcAction.getService();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		Object serviceBean = BeanUtils.getBean(serviceClass);
		if(ToolsKit.isEmpty(serviceBean)) {
			throw new EmptyNullException("serviceBean is null");
		}
		logger.warn("["+ RpcUtils.formatDate(System.currentTimeMillis()) + "] receive rpc request["+request.getRequestId()+"] :  " +  ifaceClass.getName()+"."+methodName);
		Method method = ifaceClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(serviceBean, parameters);
	}
}