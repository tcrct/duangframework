package com.duangframework.rpc.handler;

import com.duangframework.core.common.dto.rpc.MessageHolder;
import com.duangframework.core.kit.ToolsKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * duang-rpc netty 处理器
 * 
 * */
public class NettyServiceHandler extends SimpleChannelInboundHandler<MessageHolder> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServiceHandler.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, MessageHolder holder) throws Exception {
		System.out.println("#############NettyServiceHandler: " + ToolsKit.toJsonString(holder));
//		NettyDecoder encoder = new NettyDecoder();
		String request = (String)holder.getBody();
		System.out.println("requestId: " + holder.getSign());
		System.out.println("requestId: " + request);
	}

}