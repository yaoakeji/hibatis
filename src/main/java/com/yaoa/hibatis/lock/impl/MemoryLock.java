/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockException;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
class MemoryLock extends Lock {
	
	private boolean locked = false;
	
	private MemoryLockProvider lockProvider;

	private static ConcurrentHashMap<String , Long> lockedKeys = new ConcurrentHashMap<String , Long>();
	
	public MemoryLock(String key , MemoryLockProvider lockProvider) {
		super(key);
		this.lockProvider = lockProvider;
	}

	@Override
	public void lock() {
		long startTime = System.nanoTime();
		while (true) {
			Long threadId = lockedKeys.get(key);
			if(threadId == null){
				locked = true;
				lockedKeys.put(key, Thread.currentThread().getId());
				return;
			}
			if(threadId == Thread.currentThread().getId()){
				return;
			}
			long endTime = System.nanoTime();
			if(endTime - startTime > TimeUnit.SECONDS.toNanos(lockProvider.getTimeout())){
				throw new LockException("Acquiring a lock timeout");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new LockException(e.getMessage());
			}
		}
	}

	@Override
	public void unlock() {
		if(!locked){
			return;
		}
		lockedKeys.remove(key);
	}
}
