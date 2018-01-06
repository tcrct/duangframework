package com.duangframework.validation.core;


import com.duangframework.core.kit.ObjectKit;

import java.lang.reflect.Field;

public abstract class Validators {

	protected Field field;
	protected Object obj;
	protected Object value;
	
	
	public Validators(Field field, Object obj) {
		super();
		this.field = field;
		this.obj = obj;
		setValue();
	}
	
	public Class<?> getFieldType() {
		return field.getType();
	}
	
	public Field getField() {
		return field;
	}
	
	public Object getObj() {
		return obj;
	}
	
	private void setValue(){
		try {
			this.value = ObjectKit.getFieldValue(obj, field);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object getValue(){
		return value;
	}

	public abstract void validator() throws Exception;
}
