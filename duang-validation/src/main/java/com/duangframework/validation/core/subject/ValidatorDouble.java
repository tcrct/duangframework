package com.duangframework.validation.core.subject;


import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.exceptions.ValidatorException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Field;

public final class ValidatorDouble{
	
	public static void validator(Object obj, Field field, Validation validator, Object tmpValue) throws Exception{

		double number = 0d;
		String desc = "".equals(validator.desc()) ? field.getName().toString() : validator.desc();
		double[] range = (null == validator.range() || validator.range()[0] == 0) ? null : validator.range();

		if(ToolsKit.isNotEmpty(tmpValue)) {
			try {
				number = Double.parseDouble(tmpValue+"");
			} catch (Exception ex) {
				throw new ValidatorException(desc + "不是数字!");
			}
		}
		// 有指定范围值
		if( null != range){
			if(number < range[0] || number > range[1]) {
				throw new ValidatorException(desc+"数值不在允许的["+ range[0] +"]-["+  range[1] +"]范围内!");	
			}
		}

		try {
			// 取默认值
			String valueStr = validator.fieldValue();
			if(ToolsKit.isNotEmpty(valueStr) && number == 0) {
				number =  Double.parseDouble(valueStr);
			}
			ObjectKit.setField(obj, field, number);
		} catch (Exception e) {
			throw new ValidatorException("填充"+ desc+"时出错： "+ e.getMessage());
		}
	}

}
