/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.transaction;

import java.util.ArrayList;
import java.util.List;

import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.cache.CacheContext;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.lock.Lock;;

/**
 * 事务上下文的缓存
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年8月5日
 */
public class TransactionCacheContext extends CacheContext {

	private TransactionCache cache;
	
	private List<Lock> locks;
	
	private Object transaction;
	
	private String name;
	
	public TransactionCacheContext(String name , Object transaction) {
		this.name = name;
		this.transaction = transaction;
		this.cache = new TransactionCache();
		this.locks = new ArrayList<Lock>();
	}
	
	@Override
	public Cache getCache() {
		return cache;
	}
	
	@Override
	public void merge(String key, HibatisEntity entity, String[] properties) {
		this.cache.merge(key, entity,  properties);
	}
	
	public void commit() {
		this.cache.commit();
	}

	public void rollback() {
		this.cache.rollback();
	}

	public void cleanup() {
		this.cache.cleanup();
		// 释放所有的锁
		for (Lock lock : this.locks) {
			lock.unlock();
		}
		this.locks.clear();
	}
	
	public void realseLock(Lock lock){
		this.locks.add(lock);
	}

	public String getName() {
		return name;
	}

	public Object getTransaction() {
		return transaction;
	}
}
