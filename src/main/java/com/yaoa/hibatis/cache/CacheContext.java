/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.yaoa.hibatis.cache.impl.EntityCacheKeyGenerator;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockManager;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.Property;
import com.yaoa.hibatis.serializer.KryoFactory;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月24日
 */
@SuppressWarnings("unchecked")
public abstract class CacheContext {
	
	private static final KryoFactory kryoFactory = KryoFactory.getFactory();
	
	public abstract Cache getCache();
	
	public void put(String key , Object value){
		Cache cache = getCache();
		// 如果缓存是否缓存在内存则需要克隆对象（避免缓存对象污染）
		if(cache.isMemory()){
			value = clone(value);
		}
		cache.put(key , value);
	}
	
	public void merge(String key, HibatisEntity entity , String[] properties) {
		Class<?> entityType = EntityEnhancer.getEntityType(entity);
		String lockKey = EntityCacheKeyGenerator.getInstance().trimPrefix(key) + "$$merge";
		Lock lock = LockManager.getLock(lockKey);
		lock.lock();
		try {
			//获取实体（可能复制副本）
			HibatisEntity oldEntity = (HibatisEntity)this.get(key);
			if(oldEntity == null){
				return;
			}
			// 重置实体已改变的属性集
			EntityEnhancer.discardPropertyChanges(entity);
			Cache cache = getCache();
			// 如果缓存是否缓存在内存则需要克隆对象（避免缓存对象污染）
			if(cache.isMemory()){
				entity = clone(entity);
			}
			EntityMetadata metadata = EntityMetadata.get(entityType);
			for (String propName : properties) {
				Property property = metadata.findProperty(propName);
				Object value = property.getValue(entity);
				property.setValue(oldEntity, value);
			}
			cache.put(key, oldEntity);
		} finally {
			lock.unlock();
		}
	}

	public <T> T get(String key){
		Cache cache = getCache();
		T value = (T)cache.get(key);
		if(value == null){
			return null;
		}
		// 如果缓存是否缓存在内存则需要克隆对象（避免缓存对象污染）
		if(cache.isMemory()){
			value = clone(value);
		}
		return value;
	}

	public void remove(String key){
		Cache cache = getCache();
		cache.remove(key);
	}
	
	private <T> T  clone(T obj){
		Kryo kryo = kryoFactory.getKryo();
		try {
			Output output = new Output(1024, 1024 * 500);
			kryo.writeClassAndObject(output, obj);
			output.flush();
			Input input = new Input(output.toBytes());
			T t = (T) kryo.readClassAndObject(input);
			return t;
		} finally {
			kryoFactory.returnKryo(kryo);;
		}
	}
}
