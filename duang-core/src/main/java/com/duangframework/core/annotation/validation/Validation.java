package com.duangframework.core.annotation.validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {
	
	 boolean isEmpty() default true;		// 是否允许值为null或空字串符 默认为允许
	 
	 int length() default 0;	 			// 长度，限制字符串长度
	 
	 double[] range() default 0;			// 取值范围，如[0,100] 则限制该值在0-100之间
	 
	 String desc() default "";				// 设置字段名, 用于发生异常抛出时，中文说明该变量名称
	 
	 String value() default "";				// 默认值
	 
	 String formatDate() default "yyyy-MM-dd HH:mm:ss";		// 格式化日期(24小时制)
	 
	 boolean oid() default false;					// 是否是mongodb objectId，主要用于验证id

}



/**
validator.assertFalse=assertion failed

validator.assertTrue=assertion failed

validator.future=must be a future date

validator.length=length must be between {min} and {max}

validator.max=must be less than or equal to {value}

validator.min=must be greater than or equal to {value}

validator.notNull=may not be null

validator.past=must be a past date

validator.pattern=must match "{regex}"

validator.range=must be between {min} and {max}

validator.size=size must be between {min} and {max}
*/
