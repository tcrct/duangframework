package com.duangframework.rpc.handler;

import com.duangframework.core.common.dto.rpc.MessageHolder;
import com.duangframework.core.kit.ToolsKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * duang-rpc client netty 处理器
 * 
 * */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageHolder> {

	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

	public NettyClientHandler(Class<? extends MessageHolder> holderClass) {
		super(holderClass);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageHolder holder) throws Exception {
		try{
			System.out.println("############NettyClientHandler messageReceived:  "+ ToolsKit.toJsonString(holder));
//			NettyEncoder encoder = new NettyEncoder();
//			encoder.write(ctx, holder, ctx.newPromise());
//			ctx.flush();
		}catch (Exception e){
			logger.warn(e.getMessage(), e);
		} finally {
			ReferenceCountUtil.release(holder);
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

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("##################channelRegistered: " + ctx.channel().id());
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