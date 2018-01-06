package com.duangframework.validation.core.subject;

import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.exceptions.ValidatorException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Field;

public final class ValidatorFloat{
	
	public static void validator(Object obj, Field field, Validation validator, Object tmpValue) throws Exception{

		float number = 0f;
		String desc = "".equals(validator.desc()) ? field.getName().toString() : validator.desc();
		double[] range = (null == validator.range() || validator.range()[0] == 0) ? null : validator.range();

		if(ToolsKit.isNotEmpty(tmpValue)) {
			try {
				number = Float.parseFloat(tmpValue+"");
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ValidatorException(desc + "不是Float数字!");
			}
		}
		
		if( null != range){
			if(number < range[0] || number > range[1]) {
				throw new ValidatorException(desc+"数值不在允许的["+ (float)range[0] +"]-["+  (float)range[1] +"]范围内!");	
			}
		}
		
		try {
			String valueStr = validator.value();
			if(ToolsKit.isNotEmpty(valueStr) && number == 0) {
				number =  Float.parseFloat(valueStr);
			}
			ObjectKit.setField(obj, field, number);
		} catch (Exception e) {
			throw new ValidatorException("填充"+ desc+"时出错： "+ e.getMessage());
		}
	}

}
