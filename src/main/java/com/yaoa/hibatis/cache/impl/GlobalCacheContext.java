/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.cache.CacheContext;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月24日
 */
public class GlobalCacheContext extends CacheContext{

	private Cache cache = new MemoryCache();
	
	private GlobalCacheContext(){}
	
	private final static GlobalCacheContext INSTANCE = new GlobalCacheContext();
	
	public static GlobalCacheContext getInstance(){
		return INSTANCE;
	}
	
	public static void register(Cache cache){
		INSTANCE.cache = cache;
	}
	
	@Override
	public Cache getCache() {
		if(INSTANCE.cache == null){
			return new MemoryCache();
		}
		return cache;
	}
}
