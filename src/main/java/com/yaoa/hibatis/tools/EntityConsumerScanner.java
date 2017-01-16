/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.tools;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.entity.impl.ProxyClassGenerator;

/**
 * 可缓存实体扫描器
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月8日
 */
public class EntityConsumerScanner implements InitializingBean {

	private static Log logger = LogFactory.getLog(EntityConsumerScanner.class);
	
	private String[] basePackages;

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		ClassScanner scanner = new ClassScanner(getBasePackages(), Entity.class);
		Set<Class<?>> entityTypes = scanner.scan();
		for (Class<?> entityType : entityTypes) {
			new ProxyClassGenerator(entityType , false).generateClass();
			logger.debug("Build entity consumer type:" + entityType);
		}
		
	}
	
	public String[] getBasePackages() {
		return basePackages;
	}
	
	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

}
