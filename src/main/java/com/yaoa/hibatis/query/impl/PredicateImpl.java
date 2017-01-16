/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.impl;

import com.yaoa.hibatis.query.Connective;
import com.yaoa.hibatis.query.Operator;
import com.yaoa.hibatis.query.Predicate;

/**
 * 
 * 条件判断
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class PredicateImpl implements Predicate{
	
	private Connective connective;
	
	private String path;
	
	private Operator operator;
	
	private Object[] values;
	
	public PredicateImpl(Connective connective, String path , Operator operator , Object[] values){
		this.connective = connective;
		this.path = path;
		this.operator = operator;
		this.values = values;
	}

	public String getPath() {
		return path;
	}

	public Operator getOperator() {
		return operator;
	}

	public Object[] getValues() {
		return values;
	}
	
	
	@Override
	public String toString() {
		return path + " " + operator.render(values);
	}

	public Connective getConnective() {
		// TODO Auto-generated method stub
		return connective;
	}
}
