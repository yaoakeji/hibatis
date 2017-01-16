/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.exception;

/**
 * 验证异常
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月13日
 */
public class HibatisValidateException extends HibatisException{
	
	private static final long serialVersionUID = 2997930546060267645L;

	public HibatisValidateException(String message) {
		super(message);
	}

}
