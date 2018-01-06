package com.duangframework.validation.core.subject;


import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.exceptions.ValidatorException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author laotang
 */
public final class ValidatorDate{
	
	
	public static void validator(Object obj, Field field, Validation validator) throws Exception{

		String desc = "".equals(validator.desc()) ? field.getName().toString() : validator.desc();
		boolean isEmpty = validator.isEmpty();
		long dateLong = 0L;
		boolean paramNull = false;
		String formatStr =  validator.formatDate();

		if(!isEmpty){
			throw new ValidatorException("[" + desc+"] 不能为空!");
		}
		
		try {
			Date date = (Date) ObjectKit.getFieldValue(obj, field);
			paramNull =( null == date) ? true : false;
			if(null != date){
				dateLong =  date.getTime();
			}
		} catch (Exception e) {
			throw new ValidatorException("[" + desc+"] 不是java.util.Date!");
		}

		try {			
			if(!paramNull && dateLong > 0L){
				String value = ToolsKit.formatDate(new Date(dateLong), formatStr);
				ObjectKit.setField(obj, field, ToolsKit.parseDate(value, formatStr));
			}
		} catch (Exception e) {
			throw new ValidatorException(desc+"不能正确转换为["+formatStr+"]日期格式!");
		}
	}

}
