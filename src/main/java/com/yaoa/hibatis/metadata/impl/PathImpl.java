/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata.impl;

import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.Path;
import com.yaoa.hibatis.metadata.ReferenceProperty;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月22日
 */
public class PathImpl implements Path{
	
	private ReferenceProperty reference;
	
	private ColumnProperty property;
	
	public PathImpl(ReferenceProperty reference , ColumnProperty property){
		this.reference = reference;
		this.property = property;
	}

	
	public String getColumn() {
		String tableAlias;
		if(reference == null){
			tableAlias = "_this";
		}else{
			tableAlias = "_" + reference.getName();
		}
		String columnName = property.getColumnName();
		return tableAlias + "." + columnName;
	}

	
	public String getMapping() {
		String tableAlias;
		if(reference == null){
			tableAlias = "_this";
		}else{
			tableAlias = "_" + reference.getName();
		}
		String propertyName = property.getName();
		return tableAlias + "_" + propertyName;
	}

	public String getName() {
		if(reference == null){
			return property.getName();
		}else{
			return reference.getName() + "." +  property.getName();
		}
	}

	public String getPropertyName() {
		return property.getName();
	}
}
