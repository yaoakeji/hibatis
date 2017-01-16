/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2017年1月16日
 */
public class SqlSessionFactoryBean extends org.mybatis.spring.SqlSessionFactoryBean {

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	public SqlSessionFactoryBean() {
		this.setConfigLocation(resourcePatternResolver.getResource("mybatis.xml"));
		try {
			this.setMapperLocations(resourcePatternResolver.getResources("classpath*:sqlmap/**/*.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Properties props = new Properties();
		props.put("mapUnderscoreToCamelCase", true);
		this.setConfigurationProperties(props);
	}
}
