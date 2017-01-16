/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache;

import java.util.Map;

import com.yaoa.hibatis.cache.impl.EntityCacheManagerImpl;
import com.yaoa.hibatis.entity.HibatisEntity;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public abstract class EntityCacheManager {
	
	public static EntityCacheManager getInstance(){
		return EntityCacheManagerImpl.getInstance();
	}
	
	public abstract <T> boolean cacheable(Class<T> entityType);

	public abstract void put(HibatisEntity entity);
	
	public abstract void merge(HibatisEntity entity , String[] properties);

	public abstract <T> T get(Class<T> entityType, Map<String,Object> id);

	public abstract void remove(HibatisEntity entity);

	public abstract <T> void remove(Class<T> entityType, Map<String,Object> id);
	
	public abstract <T> CacheCollection<T> getCollection(Class<T> entityType, String collectionName);

	public abstract <T> void putCollection(Class<T> entityType, String collectionName, CacheCollection<T> collection);

	public abstract <T> void cleanCollection(Class<T> entityType);
}
