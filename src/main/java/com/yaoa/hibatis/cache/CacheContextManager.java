/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache;

import com.yaoa.hibatis.cache.impl.GlobalCacheContext;
import com.yaoa.hibatis.transaction.TransactionCacheContextHolder;

/**
 * 
 * 缓存上下文管理器
 * @author kingsy.lin
 * @version 1.0 , 2016年10月24日
 */
public class CacheContextManager {

	public static CacheContext getContext() {
		CacheContext transactionContext = TransactionCacheContextHolder.getContext();
		if (transactionContext != null) {
			return transactionContext;
		}
		return GlobalCacheContext.getInstance();
	}

}
