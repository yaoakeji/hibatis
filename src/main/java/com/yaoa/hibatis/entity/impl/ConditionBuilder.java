/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.metadata.Path;
import com.yaoa.hibatis.metadata.Root;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class ConditionBuilder {
	
	public static Map<String, Object> id2Parameter2(Class<?> entityType , Object[] id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		EntityMetadata metadata = EntityMetadata.get(entityType);
		List<IdProperty> idProperties = metadata.getIdProperties();
		for (int i = 0; i < idProperties.size(); i++) {
			IdProperty property = idProperties.get(i);
			String name = property.getName();
			Object value = id[i];
			parameters.put(name, value);
		}
		return parameters;
	} 

	public static String byId(Class<?> entityType) {
		EntityMetadata metadata = EntityMetadata.get(entityType);
		Root root = Root.get(entityType);
		StringBuilder conditionBuilder = new StringBuilder();
		for (IdProperty idProperty : metadata.getIdProperties()) {
			Path path = root.getPath(idProperty);
			String columnName = path.getColumn();
			String propertyName = path.getPropertyName();
			conditionBuilder.append(columnName).append("=#{").append(propertyName).append("}");
			conditionBuilder.append(" AND ");
		}
		if (conditionBuilder.length() == 0) {
			throw new HibatisException("类[" + entityType.getName() + "]没有设置@Id");
		}
		conditionBuilder.delete(conditionBuilder.lastIndexOf("AND"), conditionBuilder.length());
		return conditionBuilder.toString();
	}
}
