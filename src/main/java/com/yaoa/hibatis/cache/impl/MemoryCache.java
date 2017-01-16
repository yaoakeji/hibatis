/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.Property;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class MemoryCache implements Cache{

	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();

	public void put(String key, Object value) {
		map.put(key, value);
	}
	
	public void merge(String key, HibatisEntity entity , String[] properties) {
		HibatisEntity oldEntity  = (HibatisEntity)map.get(key);
		//不存在缓存实体则不合并
		if(oldEntity == null){
			return;
		}
		//把新实体的值复制到旧实体中
		EntityMetadata metadata = EntityEnhancer.getEntityMetadata(entity);
		for (String propertyName : properties) {
			Property property = metadata.findProperty(propertyName);
			Object propValue = property.getValue(entity);
			property.setValue(oldEntity, propValue);
		}
	}

	public Object get(String key) {
		return  map.get(key);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public boolean isMemory() {
		return true;
	}

}
