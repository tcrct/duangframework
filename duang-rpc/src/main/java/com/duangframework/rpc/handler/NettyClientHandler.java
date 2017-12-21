package com.duangframework.rpc.handler;

import com.duangframework.core.exceptions.RpcException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.client.RpcClient;
import com.duangframework.rpc.common.MessageHolder;
import com.duangframework.rpc.common.Protocol;
import com.duangframework.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * duang-rpc client netty 处理器
 * 接收到服务器处理结果返回
 * 
 * */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageHolder<RpcResponse>> {

	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageHolder<RpcResponse> holder) throws Exception {
		try{
			if(Protocol.OK == holder.getSign()){
				receiveRpcResponse(holder.getBody());
			}
		}catch (Exception e){
			logger.warn(e.getMessage(), e);
		} finally {
			ReferenceCountUtil.release(holder);
		}
	}

	/**
	 * 接收返回
	 * 利用LinkedBlockingQueue的机制
	 * 先在客户端发送前创建一个以requestId为Key,LinkedBlockingQueue对象为值的k-v对
	 *  再poll(timeout, requestId)取出对应的值
	 * @param response		RPC返回对象值
	 * @throws Exception
	 */
	private void receiveRpcResponse(RpcResponse response) {
		if(ToolsKit.isEmpty(response)) {
			throw new NullPointerException("receive rpc response is null");
		}
		String requestId = response.getRequestId();
		try {
			if(RpcClient.getResponseMap().containsKey(requestId)){
				LinkedBlockingQueue<RpcResponse> queue = RpcClient.getResponseMap().get(requestId);
				if (queue != null) {
					queue.put(response);
				} else {
					throw new RpcException("give up the response,request id is:" + requestId + ",because queue is null");
				}
			} else {
				throw new RpcException("give up the response,request id is:" + requestId + ", maybe because timeout!");
			}
		} catch (Exception e) {
			throw new RpcException("request["+requestId+"] put response to queue error:" + e.getMessage(), e);
		}
	}

	/**
	 * netty抛出异常时处理方法
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		logger.warn("["+ ctx.channel().remoteAddress()+"] client exceptionCaught exception: " + cause.getMessage(), cause);
		if(ctx.channel().isOpen()){
			ctx.close();
		}
	}

	/**
	 * 服务没发现或没有注册时抛出
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.warn("client channelUnregistered exception:  " + ctx.channel()+"           isOpen:  "+ ctx.channel().isOpen());
		if(ctx.channel().isOpen()){
//			RpcUtils.deleteZooKeepNote(ctx.channel());
			ctx.channel().close();
		}
	}
}