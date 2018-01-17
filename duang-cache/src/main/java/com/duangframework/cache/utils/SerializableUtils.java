package com.duangframework.cache.utils;

import com.alibaba.fastjson.TypeReference;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;
import redis.clients.util.SafeEncoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SerializableUtils {

	private SerializableUtils() {

	}

	public static <T> byte[] serialize(T obj) {
		return SafeEncoder.encode(ToolsKit.toJsonString(obj));
	}

	public static <T> T deserialize(byte[] data, Class<T> clazz) {
		try {
			return ToolsKit.jsonParseObject(data, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T deserialize(byte[] data, TypeReference<T> type) {
		try {
			return ToolsKit.jsonParseObject(new String(data, Const.ENCODING_FIELD), type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> List<T> deserializeArray(byte[] data, Class<T> clazz) {
		try {
			return ToolsKit.jsonParseArray(new String(data,Const.ENCODING_FIELD), clazz);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
