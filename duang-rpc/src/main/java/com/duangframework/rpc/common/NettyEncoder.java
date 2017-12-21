package com.duangframework.rpc.common;

import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty编码器
 * @author  laotang
 */
@SuppressWarnings("rawtypes")
public class NettyEncoder extends MessageToByteEncoder<MessageHolder> {

	private static final Logger logger = LoggerFactory.getLogger(NettyEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageHolder msg, ByteBuf out) throws Exception {
//		ByteBuf out = ctx.alloc().buffer(4 * request.length());
		byte[] data = null;
		try {
			data = ToolsKit.toJsonBytes(msg.getBody());
		}catch (Exception e) {
			logger.warn("netty encoder is fail： " + e.getMessage(), e);
		}
		if(null == data) {
			throw new NullPointerException("netty encoder is fail");
		}
		out.writeShort(Protocol.MAGIC)
				.writeByte(msg.getSign())
				.writeByte(msg.getStatus())
				.writeInt(data.length)
				.writeBytes(data);
	}

}