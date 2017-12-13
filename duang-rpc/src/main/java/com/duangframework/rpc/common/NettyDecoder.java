package com.duangframework.rpc.common;


import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.serializable.JdkSerializableUtil;
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
		System.out.println(sss +  "    bodyLength: " + bodyLength);
		if (sss != bodyLength) {
			// 消息体长度不一致
			logger.warn("消息体长度不一致");
			in.resetReaderIndex();
			return;
		}

		byte[] data = new byte[bodyLength];
		in.readBytes(data);

		Object obj = null;
		try {
//			obj = org.apache.commons.lang3.SerializationUtils.deserialize(data);
//			obj = SerializableUtilsProtostruff.deserialize(data, genericClass);
//			obj = JdkSerializableUtil.deserialize(data);
//			obj = HessianSerializableUtil.deserialize(data);
			obj = ToolsKit.jsonParseObject(data, RpcRequest.class);
		} catch (Exception e) {
			logger.warn("Hessian反序列化时出错： " + e.getMessage(), e);
			logger.warn("用jdk反序列化");
			try {
				obj = JdkSerializableUtil.deserialize(data);
			} catch (Exception e1) {
				logger.warn("JDK反序列化时出错： " + e.getMessage(), e);
			}
		}
		if(null == obj) {
			throw new NullPointerException("hessian or jdk decoder is fail");
		}

		MessageHolder<RpcRequest> messageHolder = new MessageHolder();
		messageHolder.setSign(sign);
		messageHolder.setStatus(status);
		messageHolder.setBody((RpcRequest) obj);
		out.add(messageHolder);
	}

}
