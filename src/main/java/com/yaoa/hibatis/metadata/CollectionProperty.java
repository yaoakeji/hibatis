/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.yaoa.hibatis.annotation.Collection;
import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.query.Sort;

/**
 * 
 * 集合属性
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月19日
 */
public final class CollectionProperty extends ReferenceProperty {

	private Class<?> referenceType;

	private Class<?> rawType;
	
	private boolean isLazy;

	private boolean isIn;
	
	private boolean cacheable;

	private Sort[] sorts = new Sort[0];

	public CollectionProperty(EntityMetadata metadata, Field field) {
		super(metadata, field);
		Collection collection = field.getAnnotation(Collection.class);
		if(!java.util.Collection.class.isAssignableFrom(field.getType())){
			throw new HibatisException("Collection属性必须为集合类型");
		}
		this.isLazy = collection.lazy();
		this.isIn = collection.in();
		this.cacheable = collection.cacheable();
		ParameterizedType type = (ParameterizedType) field.getGenericType();
		this.referenceType = (Class<?>) type.getActualTypeArguments()[0];
		this.rawType = field.getType();
		if(this.rawType != List.class){
			throw new HibatisException("@Collection只支持java.util.List类型");
		}
		this.setProperties(collection.property());
		this.setReferences(collection.reference());
		String[] orderBy = collection.orderBy();
		if(orderBy != null){
			sorts = new Sort[orderBy.length];
			for (int i = 0; i < sorts.length; i++) {
				String order = orderBy[i];
				sorts[i] = Sort.parse(order);
			}
		}
	}
	
	@Override
	public boolean cacheable() {
		return super.cacheable() && cacheable;
	}

	@Override
	public Class<?> getReferenceType() {
		return referenceType;
	}
	
	public Class<?> getRawType() {
		return rawType;
	}

	public boolean isIn() {
		return isIn;
	}
	
	public boolean isLazy() {
		return isLazy;
	}

	public Sort[] getSorts() {
		return sorts;
	}
}
