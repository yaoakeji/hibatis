/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 命令锁
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月13日
 */
class StatementResultLock {

	private String id;

	private ReentrantLock lock;

	private static Map<String, StatementResultLock> lockMap;

	static {
		lockMap = new HashMap<String, StatementResultLock>();
	}

	private StatementResultLock(String id) {
		this.id = id;
		this.lock = new ReentrantLock();
	}

	public void lock() {
		this.lock.lock();
	}

	public void unlock() {
		this.lock.unlock();
		lockMap.remove(id);
	}
	
	public static StatementResultLock getLock(String id){
		StatementResultLock lock = lockMap.get(id);
		if(lock != null){
			return lock;
		}
		return createLock(id);
	}
	
	private static synchronized StatementResultLock createLock(String id){
		StatementResultLock lock = lockMap.get(id);
		if(lock != null){
			return lock;
		}
		lock = new StatementResultLock(id);
		lockMap.put(id, lock);
		return lock;
	}
}
