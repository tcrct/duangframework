package com.duangframework.rpc.common;


import com.duangframework.core.kit.ToolsKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Netty解码器
 * @author pc_01
 */
public class NettyDecoder extends ByteToMessageDecoder {

	private static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);
	
	public NettyDecoder() {
	}
	
	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("############decode");
		if (in.readableBytes() < Protocol.HEADER_LENGTH) {
			logger.warn("数据包长度小于协议头长度");
			return;
		}
		in.markReaderIndex();

		if (in.readShort() != Protocol.MAGIC) {
			// Magic不一致，表明不是自己的数据
			logger.warn("Magic不一致");
			return;
		}

		// 开始解码
		byte sign = in.readByte();
		byte status = in.readByte();
		// 确认消息体长度
		int bodyLength = in.readInt();
		int sss = in.readableBytes();
		if (sss != bodyLength) {
			// 消息体长度不一致
			logger.warn("消息体长度不一致");
			in.resetReaderIndex();
			return;
		}

		byte[] data = new byte[bodyLength];
		in.readBytes(data);

		RpcRequest obj = null;
		try {
			obj = ToolsKit.jsonParseObject(data, RpcRequest.class);
		} catch (Exception e) {
			logger.warn("netty decoder is fail： " + e.getMessage(), e);
		}
		if(null == obj) {
			throw new NullPointerException("netty decoder is fail");
		}

		MessageHolder<RpcRequest> messageHolder = new MessageHolder();
		messageHolder.setSign(sign);
		messageHolder.setStatus(status);
		messageHolder.setBody(obj);
		out.add(messageHolder);
	}

}
