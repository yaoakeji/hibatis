/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import java.util.Map;

import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.entity.impl.EntityKeyGenerator;

/**
 * 
 * 实体缓存键生成器
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class EntityLockKeyGenerator {
	
	private EntityKeyGenerator  keyGenerator = EntityKeyGenerator.getInstance();
	
	private static EntityLockKeyGenerator instance = new EntityLockKeyGenerator();
	
	private EntityLockKeyGenerator(){}
	
	public static EntityLockKeyGenerator getInstance(){
		return instance;
	}
	
	public String generate(HibatisEntity entity) {
		Class<?> entityType = EntityEnhancer.getEntityType(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(entityType.getName());
		sb.append("#");
		sb.append(keyGenerator.generate(entity));
		return sb.toString();
	} 
	
	public String generate(Class<?> entityType , Map<String , Object> id) {
		StringBuilder sb = new StringBuilder();
		sb.append(entityType.getName());
		sb.append("#");
		sb.append(keyGenerator.generate(entityType , id));
		return sb.toString();
	}
	
	public String generate(Class<?> entityType , String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(entityType.getName());
		sb.append("#");
		sb.append(name);
		return sb.toString();
	}
}
