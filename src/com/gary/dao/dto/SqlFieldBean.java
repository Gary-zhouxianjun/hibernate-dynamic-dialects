package com.gary.dao.dto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)   
@Documented 
@Inherited
public @interface SqlFieldBean {
	public static final String EQUAL = "=";
	public static final String LIKE_BEFORE = "BEFORE";
	public static final String LIKE_AFTER = "AFTER";
	public static final String LIKE_ALL = "ALL";
	public static final String LIKE = "like";
	public static final String MAX = ">";
	public static final String MIN = "<";
	public static final String NO_EQUAL = "<>";
	public static final String IN = "in";
	public static final String NOT_IN = "not in";
	public String field() default "";
	public String compare() default EQUAL;
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)   
	@Documented 
	@Inherited
	public @interface Date {
		public String value() default "yyyy-MM-dd HH:mm:ss";
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)   
	@Documented 
	@Inherited
	public @interface Set {
		public Class<?> value();
	}
}
