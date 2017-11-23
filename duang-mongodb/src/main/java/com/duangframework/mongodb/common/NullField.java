package com.duangframework.mongodb.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 允许为null对象字段集合
 * @author laotang
 *
 */
public class NullField {

	private Collection<String> fields = null;
	
	public NullField() {
		fields = new ArrayList<String>();
	}
	
	/**
	 * 添加查询返回字段
	 * @param fieldName		字段名
	 * @return
	 */
	public NullField add(String fieldName) {
		fields.add(fieldName);
		return this;
	}

}
