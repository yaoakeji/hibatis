/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock.impl;

import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockProvider;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
public class MemoryLockProvider implements LockProvider{

	private int timeout = 120;
	
	public Lock getLock(String key) {
		return new MemoryLock(key , this);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
