/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yaoa.hibatis.exception.HibatisException;

/**
 * 
 * 关联属性
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public abstract class ReferenceProperty extends AbstractProperty {

	private boolean isPKProperties = false;
	
	private boolean isPKReferences = false;
	
	private List<ColumnProperty> properties = new ArrayList<ColumnProperty>();

	private List<ColumnProperty> references = new ArrayList<ColumnProperty>();

	public abstract Class<?> getReferenceType() ;
	
	public ReferenceProperty(EntityMetadata metadata, Field field) {
		super(metadata, field);
	}
	
	public Property findSubProperty(String name){
		EntityMetadata metadata = EntityMetadata.get(getReferenceType());
		return metadata.findProperty(name);
	}
	
	public boolean cacheable(){
		EntityMetadata metadata = EntityMetadata.get(getReferenceType());
		return metadata.cacheable();
	}

	protected void setProperties(String[] propertyNames) {
		if (propertyNames.length == 0) {
			isPKProperties = true;
			properties.addAll(metadata.getIdProperties());
		} else {
			for (String propertyName : propertyNames) {
				ColumnProperty property = (ColumnProperty) metadata.findProperty(propertyName);
				if (property == null) {
					throw new HibatisException("The property [" + propertyName + "] is not exists");
				}
				properties.add(property);
			}
		}
		properties = Collections.unmodifiableList(properties);
	}

	protected void setReferences(String[] referenceNames) {
		EntityMetadata refMetadata = EntityMetadata.get(this.getReferenceType());
		if (referenceNames.length == 0) {
			isPKReferences  = true;
			references.addAll(refMetadata.getIdProperties());
		} else {
			for (String propertyName : referenceNames) {
				ColumnProperty property = (ColumnProperty) refMetadata.findProperty(propertyName);
				if (property == null) {
					throw new HibatisException("The property[" + propertyName + "] is not exists");
				}
				references.add(property);
			}
		}
		references = Collections.unmodifiableList(references);
	}

	public List<ColumnProperty> getProperties() {
		return properties;
	}

	public List<ColumnProperty> getReferences() {
		return references;
	}

	public boolean isPKProperties() {
		return isPKProperties;
	}

	public boolean isPKReferences() {
		return isPKReferences;
	}
}
