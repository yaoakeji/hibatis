/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.beans.BeanUtils;

/**
 * 
 * 属性元数据
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
abstract class AbstractProperty implements Property{
	
	private PropertyDescriptor descriptor;
	
	private Field field;

	private Class<?> type;

	protected EntityMetadata metadata;

	public AbstractProperty(EntityMetadata metadata, Field field) {
		this.metadata = metadata;
		String name = field.getName();
		Class<?> entityType = metadata.getEntityType();
		this.field = field;
		descriptor = BeanUtils.getPropertyDescriptor(entityType, name);
		if(descriptor == null && name.startsWith("is") && field.getType() == boolean.class){
			name = name.substring(2);
			descriptor = BeanUtils.getPropertyDescriptor(entityType, name);
		}
		if(descriptor == null){
			throw new RuntimeException("属性[" + name +"]不存在");
		}
		this.type = field.getType();
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return type;
	}

	public EntityMetadata getMetadata() {
		return metadata;
	}
	
	public Object getFieldValue(Object entity){
		try {
			field.setAccessible(true);
			return field.get(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object getValue(Object entity) {
		try {
			return descriptor.getReadMethod().invoke(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setValue(Object entity, Object value) {
		try {
			descriptor.getWriteMethod().invoke(entity, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PropertyDescriptor getDescriptor() {
		return descriptor;
	}
}
