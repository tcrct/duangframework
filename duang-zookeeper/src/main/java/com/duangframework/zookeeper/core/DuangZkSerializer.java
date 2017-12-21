package com.duangframework.zookeeper.core;

import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.exception.ZooKeeperException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author laotang
 */
public class DuangZkSerializer implements ZkSerializer {


	@Override
	public byte[] serialize(Object data) throws ZkMarshallingError {
		try{
			if(data instanceof String){ return ((String) data).getBytes(Const.ENCODING_FIELD);}
			if(data instanceof File) {throw new IllegalArgumentException("暂时不支持对File进行操作");}
			if(data instanceof InputStream) {throw new IllegalArgumentException("暂时不支持对InputStream进行操作");}
			String json = ToolsKit.toJsonString(data);
			if(ToolsKit.isNotEmpty(json)) {
				return json.getBytes(Charset.forName(Const.ENCODING_FIELD));
			}
		} catch (Exception e) {
			throw new ZooKeeperException(e.getMessage(), e);
		}
		return null;
	}



	@Override
	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		try{
			String str = new String(bytes, Charset.forName(Const.ENCODING_FIELD));
			if(ToolsKit.isNotEmpty(str)) {
				return str;
			}
		} catch (Exception e) {
			throw new ZooKeeperException(e.getMessage(), e);
		}
		return null;
	}

}
