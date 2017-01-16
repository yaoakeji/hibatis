/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.util;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月28日
 */
public class StringConvertUtils {

	public static <T> Object convert(String value , Class<?> type){
		if(type == String.class){
			return value;
		}
		if(type == Integer.class || type == int.class){
			return Integer.parseInt(value);
		}
		if(type == Double.class || type == double.class){
			return Double.parseDouble(value);
		}
		if(type == Double.class || type == double.class){
			return Double.parseDouble(value);
		}
		if(type == Long.class || type == long.class){
			return Long.parseLong(value);
		}
		if(type == Boolean.class || type == boolean.class){
			return Boolean.parseBoolean(value);
		}
		return value;
	}
}
