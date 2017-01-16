/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.beans.PropertyDescriptor;

/**
 * 
 * 属性元数据
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public interface Property {

	public String getName() ;

	public Class<?> getType();

	public EntityMetadata getMetadata() ;

	public Object getValue(Object entity) ;

	public void setValue(Object entity, Object value) ;
	
	public PropertyDescriptor getDescriptor();
}
