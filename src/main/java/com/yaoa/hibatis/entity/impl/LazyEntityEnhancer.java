/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.metadata.AssociationProperty;

/**
 * 实体增强器
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月5日
 */
public class LazyEntityEnhancer {

	private Class<?> entityType;

	private Class<?> proxyClass;

	private final static Log logger = LogFactory.getLog(LazyEntityEnhancer.class);

	private LazyEntityEnhancer(Class<?> entityType) {
		this.entityType = entityType;
	}

	public Object newInstance(HibatisEntity source, AssociationProperty association) {
		if (proxyClass == null) {
			generateProxyClass();
		}
		try {
			Object obj = proxyClass.newInstance();
			// 源对象
			Field field;
			field = obj.getClass().getDeclaredField("$source");
			field.setAccessible(true);
			field.set(obj, source);
			// 关联属性
			field = obj.getClass().getDeclaredField("$association");
			field.setAccessible(true);
			field.set(obj, association);
			// 返回实例
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void generateProxyClass() {
		if (proxyClass != null) {
			return;
		}
		try {
			proxyClass = new LazyEntityGenerator(entityType).generateClass();
			logger.info("Build hibatis entity lazy type:" + proxyClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object onPropertyGetterInvoke(String methodName , String propertyName , Object proxy) {
		try {
			Field field ;
			field =  proxy.getClass().getDeclaredField("$target");
			field.setAccessible(true);
			Object target = field.get(proxy);
			if(target == null){
				field =  proxy.getClass().getDeclaredField("$source");
				field.setAccessible(true);
				HibatisEntity source = (HibatisEntity) field.get(proxy);
				field =  proxy.getClass().getDeclaredField("$association");
				field.setAccessible(true);
				AssociationProperty association = (AssociationProperty)field.get(proxy);
				target =  EntityEnhancer.loadAssociation(association, source);
			}
			return target.getClass().getMethod(methodName).invoke(target);
		} catch (Exception e) {
			throw  new RuntimeException(e);
		}
	}

	/////////////////////////////// 实例管理 ///////////////////////////////////

	private static ConcurrentHashMap<Class<?>, LazyEntityEnhancer> instancePool;

	static {
		instancePool = new ConcurrentHashMap<Class<?>, LazyEntityEnhancer>();
	}

	public static LazyEntityEnhancer getEnhancer(Class<?> entityType) {
		LazyEntityEnhancer enhancer = instancePool.get(entityType);
		if (enhancer != null) {
			return enhancer;
		}
		enhancer = createEntityEnhancer(entityType);
		return enhancer;
	}

	private static synchronized LazyEntityEnhancer createEntityEnhancer(Class<?> entityType) {
		LazyEntityEnhancer enhancer = instancePool.get(entityType);
		if (enhancer != null) {
			return enhancer;
		}
		enhancer = new LazyEntityEnhancer(entityType);
		instancePool.put(entityType, enhancer);
		return enhancer;
	}
}
