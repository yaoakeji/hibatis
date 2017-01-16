/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.util.Map;

import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.metadata.Property;
import com.yaoa.hibatis.util.StringConvertUtils;

/**
 * 
 * 实体字符串键生成器
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class EntityKeyGenerator {

	private static EntityKeyGenerator instance = new EntityKeyGenerator();

	private EntityKeyGenerator() {
	}

	public static EntityKeyGenerator getInstance() {
		return instance;
	}
	
	public String generate(Object entity) {
		Class<?> entityType = EntityEnhancer.getEntityType(entity);
		StringBuilder sb = new StringBuilder();
		EntityMetadata metadata = EntityMetadata.get(entityType);
		for (IdProperty property : metadata.getIdProperties()) {
			String name = property.getName();
			Object value = property.getValue(entity);
			sb.append("&").append(name).append("=").append(value);
		}
		sb.delete(0, 1);
		return sb.toString();
	}

	public String generate(Class<?> entityType , Map<String,Object> id) {
		StringBuilder sb = new StringBuilder();
		EntityMetadata metadata = EntityMetadata.get(entityType);
		for (IdProperty property : metadata.getIdProperties()) {
			String name = property.getName();
			Object value = id.get(name);
			sb.append("&").append(name).append("=").append(value);
		}
		sb.delete(0, 1);
		return sb.toString();
	}

	public EntityID parse(Class<?> entityType , String key) {
		EntityMetadata metadata = EntityMetadata.get(entityType);
		EntityID map  = new EntityID(metadata);
		for (String str : key.split("&")) {
			int index = str.indexOf("=");
			String name = str.substring(0  , index);
			Property property  = metadata.findProperty(name);
			String valueStr = str.substring(index + 1);
			Object value = StringConvertUtils.convert(valueStr , property.getType());
			map.put(name, value);
		}
		return map;
	}

}
