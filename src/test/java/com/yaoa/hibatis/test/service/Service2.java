/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.test.model.Test;
import com.yaoa.hibatis.transaction.TransactionCacheContextHolder;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
@Service
public class Service2 {

	@Autowired
	private SqlMapperTemplate sqlMapper;

	@Transactional(propagation = Propagation.NOT_SUPPORTED , isolation = Isolation.READ_UNCOMMITTED )
	public void insert(){
		System.out.println(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());
		System.out.println(">>>>>" + TransactionCacheContextHolder.getContext());
		Test test = new Test();
		test.setA(new Random().nextInt());
		sqlMapper.insert(test);
	}

	public SqlMapperTemplate getSqlMapper() {
		return sqlMapper;
	}

	public void setSqlMapper(SqlMapperTemplate sqlMapper) {
		this.sqlMapper = sqlMapper;
	}
	
}
