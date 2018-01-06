package com.duangframework.validation.core;



import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ValidatorFactory {

	private static final Map<String,Field[]> FIELD_MAP = new HashMap<String, Field[]>();
		
	public static void validator(Object bean) throws Exception {
		
		String beanName = ClassUtils.getClassName(bean.getClass());
		
		Field[] fields = FIELD_MAP.get(beanName);
		if( null == fields){
			fields = bean.getClass().getDeclaredFields();
			if(null != fields) {
				FIELD_MAP.put(beanName, fields);
			}
		}
		for(int i=0; i<fields.length; i++){
			Field field = fields[i];			
			Validators valid = validatorValue(bean, field);
			if(null == valid) {
				continue;
			}
			valid.validator();
		}
	}
	
	private static Validators validatorValue(Object obj, Field field) throws Exception {
		Validators valid = null;
		if(field.isAnnotationPresent(Validation.class)){
			valid = new ValidatorProperty(field, obj);
		}
		return valid;
	}
	
}
