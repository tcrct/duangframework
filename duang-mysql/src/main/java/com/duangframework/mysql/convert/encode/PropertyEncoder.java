package com.duangframework.mysql.convert.encode;

import com.duangframework.mysql.utils.MysqlUtils;

import java.lang.reflect.Field;

/**
 * 普通属性转换
 * @author laotang
 */
public class PropertyEncoder extends Encoder {

	public PropertyEncoder( Object value, Field field ) {
		super(value, field);
	}

	@Override
	public String getFieldName() {
        return MysqlUtils.getFieldName(field);
    }

	@Override
	public Object getValue() {
			return value;
	}

}
