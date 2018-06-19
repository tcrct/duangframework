package com.duangframework.validation.core.subject;


import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.exceptions.ValidatorException;
import com.duangframework.core.kit.ObjectKit;

import java.lang.reflect.Field;

public final class ValidatorString {

	public static void validator(Object obj, Field field, Validation validator, Object tmpValue) throws Exception {

		String paramsString = (String)tmpValue;
		boolean paramNull = (null == paramsString || "".equals(paramsString));
		boolean isEmpty = validator.isEmpty();		//默认为true
		String desc = "".equals(validator.desc()) ? field.getName().toString() : validator.desc();

		if (!isEmpty && paramNull) {
			throw new ValidatorException(desc + "不能为空!");
		}

		if (!"".equals(validator.fieldValue()) && paramNull) {
			ObjectKit.setField(obj, field, validator.fieldValue());
		}

		if (!paramNull && validator.length() > 0) {
			if (paramsString.length() > validator.length()) {
				throw new ValidatorException(desc + "长度超出指定的范围");
			}
		}

		if (validator.oid() && !isEmpty) {
			if (paramNull) {
				throw new ValidatorException(desc + "不能为空!");
			}
			if(!DuangId.isValid(paramsString)) {
				throw new ValidatorException(desc + " is not DuangId!");
			}
		}
	}
}
