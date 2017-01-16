/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.lang.reflect.Field;

import com.yaoa.hibatis.annotation.Association;
import com.yaoa.hibatis.annotation.FetchMode;

/**
 * 
 * 关联属性
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public final class AssociationProperty extends ReferenceProperty {

	private Class<?> referenceType;

	private FetchMode fetchMode;

	private boolean isLazy;

	public AssociationProperty(EntityMetadata metadata, Field field) {
		super(metadata, field);
		Association association = field.getAnnotation(Association.class);
		this.fetchMode = association.fetchMode();
		this.isLazy = association.lazy();
		this.referenceType = field.getType();
		this.setProperties(association.property());
		this.setReferences(association.reference());
	}
	
	public FetchMode getFetchMode() {
		return fetchMode;
	}

	public boolean isLazy() {
		return isLazy;
	}

	@Override
	public Class<?> getReferenceType() {
		return referenceType;
	}
}
