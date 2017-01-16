/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.cache.impl.GlobalCacheContext;
import com.yaoa.hibatis.entity.EntityManager;
import com.yaoa.hibatis.lock.LockManager;
import com.yaoa.hibatis.lock.LockProvider;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
@Configuration
public class HibatisConfiguration  {

	@Bean
	public EntityManager createEntityManager(SqlSessionFactory sqlSessionFactory) {
		return EntityManager.initialize(sqlSessionFactory);
	}

	@Bean
	public SqlMapperTemplate createSqlMapperTemplate(EntityManager em) {
		return new SqlMapperTemplate(em);
	}
	
	@Autowired(required = false)
	public void setCache(Cache cache) {
		GlobalCacheContext.register(cache);
	}
	
	@Autowired(required = false)
	public void setLockProvider(LockProvider lockProvider) {
		LockManager.register(lockProvider);
	}
}


