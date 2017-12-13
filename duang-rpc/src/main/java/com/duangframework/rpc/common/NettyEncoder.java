package com.duangframework.rpc.common;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.serializable.JdkSerializableUtil;
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
//			data = HessianSerializableUtil.serialize(msg.getBody());
			data = ToolsKit.toJsonBytes(msg.getBody());
		}catch (Exception e) {
			logger.warn("Hessian序列化时出错： " + e.getMessage(), e);
			logger.warn("用jdk序列化返回");
			try {
				data = JdkSerializableUtil.serialize(msg.getBody());
			}catch (Exception e1) {
				logger.warn("JDK序列化时出错： " + e1.getMessage(), e1);
			}
		}
		if(null == data) {
			throw new NullPointerException("hessian or jdk encoder is fail");
		}
		out.writeShort(Protocol.MAGIC)
				.writeByte(msg.getSign())
				.writeByte(msg.getStatus())
				.writeInt(data.length)
				.writeBytes(data);
	}

}