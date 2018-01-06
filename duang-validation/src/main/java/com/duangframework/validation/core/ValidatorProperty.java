package com.duangframework.validation.core;


import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.utils.DataType;
import com.duangframework.validation.core.subject.*;

import java.lang.reflect.Field;


public final class ValidatorProperty extends Validators {

	protected Validation validator;
	
	public ValidatorProperty(Field field, Object obj) {
		super(field, obj);
		this.validator = field.getAnnotation(Validation.class);
	}

	public Validation getValidator() {
		return validator;
	}
	
	@Override
	public void validator() throws Exception {
		// String
		if( DataType.isString(getFieldType())){
			ValidatorString.validator(obj, field, getValidator(), getValue());
		}// Integer
		else if( DataType.isInteger(getFieldType()) || DataType.isIntegerObject(getFieldType()) ){
			ValidatorInteger.validator(obj, field, getValidator(), getValue());
		}// Double
		else if( DataType.isDouble(getFieldType()) || DataType.isDoubleObject(getFieldType())){
			ValidatorDouble.validator(obj, field, getValidator(), getValue());
		}// Float
		else if( DataType.isFloat(getFieldType()) || DataType.isFloatObject(getFieldType())){
			ValidatorFloat.validator(obj, field, getValidator(), getValue());
		}// Date
		else if( DataType.isDate(getFieldType())){
			ValidatorDate.validator(obj, field, getValidator());
		}
	}
}
