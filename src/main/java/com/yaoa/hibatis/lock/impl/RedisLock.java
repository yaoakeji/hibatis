/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock.impl;

import java.util.concurrent.TimeUnit;

import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockException;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
class RedisLock extends Lock {

	private boolean locked = false;

	private RedisLockProvider lockProvider;
	
	public RedisLock(String key, RedisLockProvider lockProvider) {
		super(key);
		this.lockProvider = lockProvider;
	}

	@Override
	public void lock() {
		do {
			long startTime = System.currentTimeMillis();
			boolean success = lockProvider.addLock(key);
			if (success) {
				locked = true;
				return;
			}
			long endTime = System.currentTimeMillis();
			if(endTime - startTime > TimeUnit.SECONDS.toMillis(lockProvider.getTimeout())){
				throw new LockException("Acquiring a lock timeout");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new LockException(e.getMessage());
			}
		} while (true);
	}

	@Override
	public void unlock() {
		if (!locked) {
			return;
		}
		lockProvider.removeLock(key);
	}

}
