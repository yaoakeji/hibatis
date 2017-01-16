/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.aggregate;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public interface Specification {
	
	public AggregateType getType();
	
	public String getExpression();

	public String getAlias();
}
