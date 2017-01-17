/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.transaction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月13日
 */
public final class TransactionCacheContextHolder {

	private static final ThreadLocal<List<TransactionCacheContext>> contextThreadLocal;

	static {
		contextThreadLocal = new ThreadLocal<List<TransactionCacheContext>>();
	}

	private TransactionCacheContextHolder() {

	}

	/**
	 * 获取当前上下文对象
	 * @return 缓存上下文对象
	 */
	public static TransactionCacheContext getContext() {
		List<TransactionCacheContext> txList = contextThreadLocal.get();
		if (txList == null) {
			return null;
		}
		String name = TransactionSynchronizationManager.getCurrentTransactionName();
		for (TransactionCacheContext tx : txList) {
			if(tx.getName().equals(name)){
				return tx;
			}
		}
		return null;
	}


	/**
	 * 开始事务
	 * @param name 名称
	 * @param transaction 事务对象
	 */
	public static void begin(String name , Object transaction) {
		List<TransactionCacheContext> txList = contextThreadLocal.get();
		if (txList == null) {
			txList = new ArrayList<TransactionCacheContext>();
		}
		TransactionCacheContext tx = new TransactionCacheContext(name , transaction);
		txList.add(tx);
		contextThreadLocal.set(txList);
	}

	/**
	 * 回滚事务
	 * @param transaction 事务对象
	 */
	public static void rollback(Object transaction) {
		TransactionCacheContext tx = getContext(transaction);
		if (tx != null) {
			tx.rollback();
		}
	}

	/**
	 * 提交事务
	 * @param transaction 事务对象
	 */
	public static void commit(Object transaction) {
		TransactionCacheContext tx = getContext(transaction);
		if (tx != null) {
			tx.commit();
		}
	}

	/**
	 * 清理事务
	 * @param transaction 事务对象
	 */
	public static void cleanup(Object transaction) {
		TransactionCacheContext tx = getContext(transaction);
		if (tx == null) {
			return;
		}
		tx.cleanup();
		// 移除上下文对象
		List<TransactionCacheContext> txList = contextThreadLocal.get();
		txList.remove(tx);
		// 判断没有上文对象，则从当前线程移除集合
		if (txList.isEmpty()) {
			contextThreadLocal.remove();
		}
	}
	
	/////////////////////////////// 私有方法 ////////////////////////////////////////

	/**
	 * 获取对应事务的上下文对象
	 * @param transaction 事务对象
	 * @return 缓存上下文对象
	 */
	private static TransactionCacheContext getContext(Object transaction) {
		List<TransactionCacheContext> txList = contextThreadLocal.get();
		if (txList == null) {
			return null;
		}
		for (TransactionCacheContext tx : txList) {
			if (tx.getTransaction() == transaction) {
				return tx;
			}
		}
		return null;
	}
}
