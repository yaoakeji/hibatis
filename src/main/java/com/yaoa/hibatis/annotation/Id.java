/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 主键字段（可自定多个）
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.FIELD})
@Documented
public @interface Id {
	
	boolean useGeneratedKeys() default true;
}
