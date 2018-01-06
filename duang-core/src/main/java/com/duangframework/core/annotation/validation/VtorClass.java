package com.duangframework.core.annotation.validation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VtorClass {
	Class value() default Object.class;		// 指定要验证的Class文件， 一般用于Collection层
}
