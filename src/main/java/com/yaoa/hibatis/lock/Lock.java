/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public abstract class Lock {
	
	protected String key;
	
	public Lock(String key) {
		this.key = "hibatis:lock:" + key;
	}
	
	public abstract void lock();

	public abstract void unlock();

	public String getKey() {
		return key;
	}
}
