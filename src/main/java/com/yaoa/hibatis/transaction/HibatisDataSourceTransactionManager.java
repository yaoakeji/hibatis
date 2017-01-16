/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * 
 *  事务管理器
 * @author kingsy.lin
 * @version 1.0 , 2016年8月13日
 */
public class HibatisDataSourceTransactionManager extends DataSourceTransactionManager{

	private static final long serialVersionUID = -4084625241926204069L;
	
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		super.doBegin(transaction, definition);
		TransactionCacheContextHolder.begin(definition.getName() , transaction);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		super.doCommit(status);
		TransactionCacheContextHolder.commit(status.getTransaction());
	}

	
	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		super.doRollback(status);
		TransactionCacheContextHolder.rollback(status.getTransaction());
	}

	
	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		super.doCleanupAfterCompletion(transaction);
		TransactionCacheContextHolder.cleanup(transaction);
	}
	
}
