package com.duangframework.core.annotation.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    public String name() default "";
    public String type() default "text";
    public String order() default "asc";
    public boolean unique() default false;

}