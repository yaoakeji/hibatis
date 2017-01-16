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
 * 条件属性（一对多，一对一、多对一）
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Association {

	String[] reference() default {};
	
	String[] property() default {};
	
	boolean lazy() default false;
	
	FetchMode fetchMode() default FetchMode.JOIN;
}
