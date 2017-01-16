/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.lang.reflect.Field;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;

import com.yaoa.hibatis.annotation.Id;

/**
 * 
 * 主键属性
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class IdProperty extends ColumnProperty{
	
	public KeyGenerator keyGenerator;
	
	public IdProperty(EntityMetadata metadata , Field field){
		super(metadata , field);
		Id id = field.getAnnotation(Id.class);
		if(id.useGeneratedKeys()){
			keyGenerator  = new Jdbc3KeyGenerator();
		}
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}
	
}
