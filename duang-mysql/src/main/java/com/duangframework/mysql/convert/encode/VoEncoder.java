package com.duangframework.mysql.convert.encode;


import com.duangframework.core.annotation.db.Vo;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Field;

/**
 * Vo对象属性转换
 * @author laotang
 */
public class VoEncoder extends Encoder {

	public VoEncoder( Object value, Field field ) {
		super(value, field);
	}

	@Override
	public String getFieldName() {
		String fieldName = field.isAnnotationPresent(Vo.class) ? field.getAnnotation(Vo.class).name() : null;
		return (ToolsKit.isNotEmpty(fieldName)) ? fieldName : field.getName();
	}

	@Override
	public Object getValue() {
		System.out.println("待实现");
		return null;
	}

}
