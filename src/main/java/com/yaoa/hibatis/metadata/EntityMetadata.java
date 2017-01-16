/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.metadata.impl.EntityMetadataImpl;

/**
 * 
 * 实体元数据
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public abstract class EntityMetadata {

	private Map<String, Property> properties = new HashMap<String, Property>();

	public abstract boolean cacheable();

	public abstract String getVersion();

	public abstract Class<?> getEntityType();

	public abstract String getTableName();

	public abstract List<IdProperty> getIdProperties();

	public abstract List<ColumnProperty> getSelectProperties();

	public abstract List<ColumnProperty> getInsertProperties();

	public abstract List<ColumnProperty> getUpdateProperties();

	public abstract List<AssociationProperty> getAssociations();

	public abstract List<CollectionProperty> getCollections();
	
	protected void addProperty(Property property){
		properties.put(property.getName(), property);
	}

	public Property findProperty(String name){
		return properties.get(name);
	}

	public static boolean isEntity(Class<?> type){
		return type.isAnnotationPresent(Entity.class);
	}
	
	public static EntityMetadata get(Class<?> type) {
		if (!isEntity(type)) {
			throw new HibatisException(type.getName() + " is not entity");
		}
		return EntityMetadataImpl.get(type);
	}
}
