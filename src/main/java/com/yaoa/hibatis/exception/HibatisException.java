/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.exception;

/**
 * 通用异常
 * @author kingsy.lin
 * @version 1.0 , 2017年1月16日
 */
public class HibatisException extends RuntimeException{

	private static final long serialVersionUID = -5610566138073821570L;
	
	public HibatisException(String message){
		super(message);
	}

}
