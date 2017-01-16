/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
public class LockException extends RuntimeException{

	private static final long serialVersionUID = 2363778344355818691L;

	public LockException(String message){
		super(message);
	}
}
