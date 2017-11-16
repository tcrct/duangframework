package com.duangframework.core.annotation.aop;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tx {
}
