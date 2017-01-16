/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月29日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Collection {

	String[] reference() default {};

	String[] property() default {};

	boolean in() default false;
	
	boolean lazy() default true;

	public boolean cacheable() default true;
	
	String[] orderBy() default {};
}
