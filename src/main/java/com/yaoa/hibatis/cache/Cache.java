/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public interface Cache {
	
	public boolean isMemory();

	public void put(String key , Object value);
	
	public Object get(String key);

	public void remove(String key);
}
