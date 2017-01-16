/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yaoa.hibatis.cache.CacheCollection;
import com.yaoa.hibatis.cache.CacheContext;
import com.yaoa.hibatis.cache.CacheContextManager;
import com.yaoa.hibatis.cache.EntityCacheManager;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.EntityManager;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.entity.impl.EntityKeyGenerator;
import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockManager;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.metadata.EntityMetadata;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
@SuppressWarnings("unchecked")
public class EntityCacheManagerImpl extends EntityCacheManager {

	private static final EntityCacheManagerImpl instance = new EntityCacheManagerImpl();

	private EntityKeyGenerator entityKeyGenerator = EntityKeyGenerator.getInstance();

	private EntityCacheKeyGenerator entityCacheKeyGenerator = EntityCacheKeyGenerator.getInstance();

	private CollectionCacheKeyGenerator collectionCacheKeyGenerator = CollectionCacheKeyGenerator.getInstance();

	private static final String COLLECTION_NAMES_KEY = "!";

	private EntityCacheManagerImpl() {

	}

	public static EntityCacheManagerImpl getInstance() {
		return instance;
	}

	@Override
	public <T> boolean cacheable(Class<T> entityType) {
		EntityMetadata metadata = EntityMetadata.get(entityType);
		return metadata.cacheable();
	}

	@Override
	public void put(HibatisEntity entity) {
		// 重置实体已改变的属性集
		EntityEnhancer.discardPropertyChanges(entity);
		// 加入
		CacheContext context = CacheContextManager.getContext();
		String key = entityCacheKeyGenerator.generate(entity);
		context.put(key, entity);
	}

	@Override
	public void merge(HibatisEntity entity, String[] properties) {
		CacheContext context = CacheContextManager.getContext();
		String key = entityCacheKeyGenerator.generate(entity);
		context.merge(key, entity, properties);
	}

	@Override
	public <T> T get(Class<T> entityType, Map<String, Object> id) {
		EntityEnhancer.getEnhancer(entityType).getProxyClass();
		CacheContext context = CacheContextManager.getContext();
		String key = entityCacheKeyGenerator.generate(entityType, id);
		T entity = context.get(key);
		if (entity != null) {
			// 判断版本是否已失效
			EntityMetadata metadata = EntityMetadata.get(entityType);
			String version = EntityEnhancer.getVersion((HibatisEntity) entity);
			if (!metadata.getVersion().equals(version)) {
				return null;
			}
		}
		return entity;
	}

	@Override
	public void remove(HibatisEntity entity) {
		CacheContext context = CacheContextManager.getContext();
		String key = entityCacheKeyGenerator.generate(entity);
		context.remove(key);
	}

	@Override
	public <T> void remove(Class<T> entityType, Map<String, Object> id) {
		CacheContext context = CacheContextManager.getContext();
		String key = entityCacheKeyGenerator.generate(entityType, id);
		context.remove(key);
	}

	@Override
	public <T> CacheCollection<T> getCollection(Class<T> entityType, String collectionName) {
		assert collectionName != null;
		CacheContext context = CacheContextManager.getContext();
		String collectionKey = collectionCacheKeyGenerator.generate(entityType, collectionName);
		CacheCollectionInfo collectionInfo = context.get(collectionKey); // 加入到缓存
		if (collectionInfo == null) {
			return null;
		}
		EntityMetadata metadata = EntityMetadata.get(entityType);
		List<T> list = new ArrayList<T>(collectionInfo.keys.size());
		for (String key : collectionInfo.keys) {
			EntityID id = entityKeyGenerator.parse(entityType, key);
			T entity = (T) EntityManager.get().findById(entityType, id, LockMode.NONE);
			if (entity == null) {
				continue;
			}
			// 判断版本是否已失效 (只要其中一个实体版本不一致致整个集合都失效)
			String version = EntityEnhancer.getVersion((HibatisEntity) entity);
			if (!metadata.getVersion().equals(version)) {
				return null;
			}
			list.add(entity);
		}
		long total = collectionInfo.getTotal();
		return new CacheCollection<T>(list , total);
	}

	@Override
	public <T> void putCollection(Class<T> entityType, String collectionName, CacheCollection<T> collection) {
		assert collectionName != null;
		CacheCollectionInfo collectionInfo = new CacheCollectionInfo(collection);
		CacheContext context = CacheContextManager.getContext();
		for (Object obj : collection.getList()) {
			HibatisEntity entity = (HibatisEntity) obj;
			String cacheKey = entityCacheKeyGenerator.generate(entity);
			context.put(cacheKey, entity);
			String key = entityKeyGenerator.generate(entity);
			collectionInfo.keys.add(key);
		}
		String collectionKey = collectionCacheKeyGenerator.generate(entityType, collectionName);
		context.put(collectionKey, collectionInfo); // 加入到缓存
		putCollectionName(entityType, collectionName);// 记录一个集合缓存
	}

	@Override
	public <T> void cleanCollection(Class<T> entityType) {
		CacheContext context = CacheContextManager.getContext();
		String collectionNamesKey = collectionCacheKeyGenerator.generate(entityType, COLLECTION_NAMES_KEY);
		Set<String> collectionNames = (Set<String>) context.get(collectionNamesKey);
		if (collectionNames == null) {
			return;
		}
		for (String collectionName : collectionNames) {
			String collectionKey = collectionCacheKeyGenerator.generate(entityType, collectionName);
			context.remove(collectionKey);
		}
		clearCollectionNames(entityType);// 记录一个集合缓存
	}

	/////////////////////////////// 私有方法 ////////////////////////////////

	private <T> void putCollectionName(Class<T> entityType, String collectionName) {
		CacheContext context = CacheContextManager.getContext();
		String lockKey = EntityLockKeyGenerator.getInstance().generate(entityType, COLLECTION_NAMES_KEY);
		Lock lock = LockManager.getLock(lockKey);
		lock.lock();
		try {
			String collectionNamesKey = collectionCacheKeyGenerator.generate(entityType, COLLECTION_NAMES_KEY);
			Set<String> collectionNames = (Set<String>) context.get(collectionNamesKey);
			if (collectionNames == null) {
				collectionNames = new HashSet<String>();
			}
			if (!collectionNames.contains(collectionName)) {
				collectionNames.add(collectionName);
			}
			context.put(collectionNamesKey, collectionNames);
		} finally {
			lock.unlock();
		}
	}

	private <T> void clearCollectionNames(Class<T> entityType) {
		CacheContext context = CacheContextManager.getContext();
		String lockKey = EntityLockKeyGenerator.getInstance().generate(entityType, COLLECTION_NAMES_KEY);
		Lock lock = LockManager.getLock(lockKey);
		lock.lock();
		try {
			String collectionNamesKey = collectionCacheKeyGenerator.generate(entityType, COLLECTION_NAMES_KEY);
			Set<String> collectionNames = (Set<String>) context.get(collectionNamesKey);
			if (collectionNames == null) {
				collectionNames = new HashSet<String>();
			} else {
				collectionNames.clear();
			}
			context.put(collectionNamesKey, collectionNames);
		} finally {
			lock.unlock();
		}
	}

}
