package com.duangframework.rpc.handler;


import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.common.MessageHolder;
import com.duangframework.rpc.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * duang-rpc netty 处理器
 * 
 * */
public class NettyServiceHandler extends SimpleChannelInboundHandler<MessageHolder<RpcRequest>> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServiceHandler.class);

	public NettyServiceHandler() {
//		RpcFactory.getRpcActionMap();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, MessageHolder<RpcRequest> holder) throws Exception {
		System.out.println("#############NettyServiceHandler: " + ToolsKit.toJsonString(holder));
//		NettyDecoder encoder = new NettyDecoder();
		RpcRequest request = holder.getBody();
		System.out.println("requestId: " + holder.getSign());
		System.out.println("request: " + ToolsKit.toJsonString(request));

//		ctx.pipeline().channel().writeAndFlush()
	}
}