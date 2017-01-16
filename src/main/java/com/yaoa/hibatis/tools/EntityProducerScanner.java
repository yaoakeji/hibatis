/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.tools;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.entity.EntityEnhancer;

/**
 * 可缓存实体扫描器
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月8日
 */
public class EntityProducerScanner implements InitializingBean{

	private static Log logger = LogFactory.getLog(EntityProducerScanner.class);
	
	private String[] basePackages;

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		ClassScanner scanner = new ClassScanner(getBasePackages(), Entity.class);
		Set<Class<?>> entityTypes = scanner.scan();
		for (Class<?> entityType : entityTypes) {
			EntityEnhancer.getEnhancer(entityType).getProxyClass();
			logger.debug("Build entity producer type:" + entityType);
		}
	}
	
	public String[] getBasePackages() {
		return basePackages;
	}
	
	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}
}
