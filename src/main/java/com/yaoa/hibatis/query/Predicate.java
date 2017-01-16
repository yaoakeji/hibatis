/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

/**
 * 
 * 条件判断
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public interface Predicate {
	
	public Connective getConnective();
	
	public String getPath();
	
	public Operator getOperator();

	public Object[] getValues();
}
