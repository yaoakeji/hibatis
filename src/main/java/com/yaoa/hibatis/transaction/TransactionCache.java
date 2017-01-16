/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.cache.CacheContext;
import com.yaoa.hibatis.cache.impl.GlobalCacheContext;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.Property;
import com.yaoa.hibatis.util.ArrayUtils;;

/**
 * 事务上下文的缓存
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年8月5日
 */
public class TransactionCache implements Cache {

	private Set<String> removeKeys = new HashSet<String>();

	private Map<String, Object> putMap = new HashMap<String, Object>();
	
	private Map<String, EntityMerge> mergeMap = new LinkedHashMap<String, EntityMerge>();
	
	public TransactionCache() {
	}

	public void put(String key, Object value) {
		removeKeys.remove(key);// 取消记录移除
		putMap.put(key, value);
	}

	//同一个实体可能会多次更新/合并
	public void merge(String key, HibatisEntity entity , String[] properties) {
		EntityMerge merge = mergeMap.get(key);
		if(merge == null){
			merge = new EntityMerge(entity , properties);
			mergeMap.put(key, merge);
		}else{
			HibatisEntity oldEntity = merge.entity;
			if(oldEntity != entity){
				//复制属性值到旧实体中
				EntityMetadata metadata = EntityEnhancer.getEntityMetadata(entity);
				for (String propName : properties) {
					Property property = metadata.findProperty(propName);
					Object value = property.getValue(entity);
					property.setValue(oldEntity, value);
				}
			}
			merge.properties.addAll(Arrays.asList(properties));
		}
	}

	public Object get(String key) {
		// 判断是否已删除
		if (removeKeys.contains(key)) {
			return null;
		}
		// 从临时映射表中获取
		Object value = putMap.get(key);
		// 临时映射表中不存在，则默认缓存中获取
		if (value == null) {
			CacheContext globalCache = GlobalCacheContext.getInstance();
			value = globalCache.get(key);
		}
		return value;
	}

	public void remove(String key) {
		// 从临时映射表中移除
		putMap.remove(key);
		// 加入已移除的键
		removeKeys.add(key);
	}

	public void commit() {
		CacheContext globalCache = GlobalCacheContext.getInstance();
		// 合并缓存
		for (Entry<String, Object> entry : putMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			globalCache.put(key, value); //放入全局缓存
		}
		for (Entry<String, EntityMerge> entry : mergeMap.entrySet()) {
			String key = entry.getKey();
			EntityMerge merge = entry.getValue();
			HibatisEntity entity = merge.entity;
			String[] properties = ArrayUtils.toArray(merge.properties);
			globalCache.merge(key, entity , properties); //放入全局缓存
		}
		// 移除缓存
		for (String key : removeKeys) {
			globalCache.remove(key);
		}
		putMap.clear();
		removeKeys.clear();
	}

	public void rollback() {
		putMap.clear();
		mergeMap.clear();
		removeKeys.clear();
	}

	public void cleanup() {
		putMap = null;
		mergeMap = null;
		removeKeys = null;
	}

	public boolean isMemory() {
		return true;
	}
	
	private static class EntityMerge {
		
		public HibatisEntity entity;

		private List<String> properties;
		
		public EntityMerge(HibatisEntity entity , String[] properties){
			this.entity = entity;
			this.properties = ArrayUtils.asList(properties);
		}
	 }
}
