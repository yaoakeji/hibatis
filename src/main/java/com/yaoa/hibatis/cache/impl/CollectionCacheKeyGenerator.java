/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

/**
 * 
 * 实体缓存键生成器
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class CollectionCacheKeyGenerator {
	
	private static final String PREFIX  = "hibatis:collection:";

	private static CollectionCacheKeyGenerator instance = new CollectionCacheKeyGenerator();
	
	private CollectionCacheKeyGenerator(){}
	
	public static CollectionCacheKeyGenerator getInstance(){
		return instance;
	}
	
	public String generate(Class<?> entityType , String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX);
		sb.append(entityType.getName());
		sb.append("#");
		sb.append(name);
		return sb.toString();
	} 
}
