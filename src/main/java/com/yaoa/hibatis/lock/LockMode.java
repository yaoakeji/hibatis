/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月26日
 */
public enum LockMode {

	/**
	 * 不使用锁
	 */
	NONE , 
	/**
	 * 读取锁 (使用程序锁)
	 */
	READ ,
	/**
	 * 利用数据库的for update子句
	 */
	UPGRADE,
	/**
	 * 利用数据库的for update nowait子句 , Oracle 支持的模式
	 */
	UPGRADE_NOWAIT
}
