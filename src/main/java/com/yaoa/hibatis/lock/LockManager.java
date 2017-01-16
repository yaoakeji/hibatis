/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock;

import java.util.Map;

import com.yaoa.hibatis.cache.impl.EntityLockKeyGenerator;
import com.yaoa.hibatis.lock.impl.MemoryLockProvider;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public final class LockManager {

	private static LockProvider instance = new MemoryLockProvider();

	public static Lock getLock(String key) {
		return instance.getLock(key);
	}

	public static Lock getEntityLock(Class<?> entityType, Map<String, Object> id) {
		String key = EntityLockKeyGenerator.getInstance().generate(entityType, id);
		return instance.getLock(key);
	}

	public static synchronized void register(LockProvider provider) {
		instance = provider;
	}
}
