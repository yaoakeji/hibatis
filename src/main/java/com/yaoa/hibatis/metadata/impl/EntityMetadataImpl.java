/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.yaoa.hibatis.annotation.Association;
import com.yaoa.hibatis.annotation.Collection;
import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.annotation.Id;
import com.yaoa.hibatis.annotation.Table;
import com.yaoa.hibatis.annotation.Transient;
import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.CollectionProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.util.CamelCaseUtils;
import com.yaoa.hibatis.util.ReflectionUtils;

public class EntityMetadataImpl extends EntityMetadata {

	private Class<?> entityType;

	private String tableName;

	private List<IdProperty> idProperites = new ArrayList<IdProperty>();

	private List<ColumnProperty> selectProperties = new ArrayList<ColumnProperty>();

	private List<ColumnProperty> insertProperties = new ArrayList<ColumnProperty>();

	private List<ColumnProperty> updateProperties = new ArrayList<ColumnProperty>();
	
	private List<AssociationProperty> associationProperties = new ArrayList<AssociationProperty>();
	
	private List<CollectionProperty> collectionProperties = new ArrayList<CollectionProperty>();

	private boolean cacheable;

	private String version;

	private EntityMetadataImpl(Class<?> clazz) {
		this.entityType = clazz;
		this.readBaseInfo();
		this.readProperties();
	}
	
	// 读取基本信息
	private void readBaseInfo() {
		Entity entity = entityType.getAnnotation(Entity.class);
		this.cacheable = entity.cacheable();
		this.version = entity.version();
		// 获取表名
		Table table = entityType.getAnnotation(Table.class);
		if (table != null) {
			tableName = table.name();
		} else {
			tableName = CamelCaseUtils.toUnderlineName(entityType.getSimpleName());
		}
	}

	// 读取属性
	private void readProperties() {
		// 加入普通属性
		Field[] fields = ReflectionUtils.getAllDeclaredFields(entityType);
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			}
			// 是否复杂类型
			if (ReflectionUtils.isComplexType(field.getType())) {
				continue;
			}
			if (field.isAnnotationPresent(Id.class)) {
				IdProperty property = new IdProperty(this, field);
				idProperites.add(property);
				selectProperties.add(property);
				// 如果不自动生成id，则需要插入
				Id id = field.getAnnotation(Id.class);
				if (!id.useGeneratedKeys()) {
					insertProperties.add(property);
				}
				this.addProperty(property);
			} else {
				ColumnProperty property = new ColumnProperty(this, field);
				insertProperties.add(property);
				updateProperties.add(property);
				selectProperties.add(property);
				this.addProperty(property);;
			}
		}
		if(this.idProperites.isEmpty()){
			throw new HibatisException("实体类"+ entityType +"没有设置主键");
		}
		// 设置属性集合不能修改
		this.idProperites = Collections.unmodifiableList(idProperites);
		this.selectProperties = Collections.unmodifiableList(selectProperties);
		this.insertProperties = Collections.unmodifiableList(insertProperties);
		this.updateProperties = Collections.unmodifiableList(updateProperties);
	}
	
	private void readReferenceProperties(){
		// 加入关联属性
		Field[] fields = ReflectionUtils.getAllDeclaredFields(entityType);
		for (Field field : fields) {
			if (field.isAnnotationPresent(Association.class)) {
				AssociationProperty property = new AssociationProperty(this, field);
				associationProperties.add(property);
				this.addProperty(property);;
			} else if (field.isAnnotationPresent(Collection.class)) {
				CollectionProperty property = new CollectionProperty(this, field);
				collectionProperties.add(property);
				this.addProperty(property);;
			} 
		}
		this.associationProperties = Collections.unmodifiableList(associationProperties);
		this.collectionProperties = Collections.unmodifiableList(collectionProperties);
	}

	//////////////////////// 返回属性 //////////////////////////////

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public List<IdProperty> getIdProperties() {
		return idProperites;
	}

	@Override
	public List<ColumnProperty> getSelectProperties() {
		return selectProperties;
	}

	@Override
	public List<ColumnProperty> getInsertProperties() {
		return insertProperties;
	}

	@Override
	public List<ColumnProperty> getUpdateProperties() {
		return updateProperties;
	}

	@Override
	public boolean cacheable() {
		return cacheable;
	}

	@Override
	public String getVersion() {
		return version;
	}

	
	@Override
	public Class<?> getEntityType() {
		return entityType;
	}

	
	@Override
	public List<AssociationProperty> getAssociations() {
		return associationProperties;
	}

	
	@Override
	public List<CollectionProperty> getCollections() {
		return collectionProperties;
	}
	
	
	///////////////////////////// 实例管理 ////////////////////////////////////
	
	private static ConcurrentHashMap<Class<?>, EntityMetadataImpl> instancePool;
	
	static {
		instancePool = new ConcurrentHashMap<Class<?>, EntityMetadataImpl>();
	}

	public static EntityMetadataImpl get(Class<?> type) {
		EntityMetadataImpl metadata = instancePool.get(type);
		if (metadata == null) {
			metadata = new EntityMetadataImpl(type);
			instancePool.put(type, metadata);
			metadata.readReferenceProperties();
		}
		return metadata;
	}
}
