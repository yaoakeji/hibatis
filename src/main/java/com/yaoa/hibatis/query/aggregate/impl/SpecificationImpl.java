/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.aggregate.impl;

import com.yaoa.hibatis.query.aggregate.AggregateType;
import com.yaoa.hibatis.query.aggregate.Specification;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public class SpecificationImpl implements Specification{
	
	private AggregateType type;
	
	private String expression;
	
	private String alias;
	
	public SpecificationImpl(AggregateType type , String expression , String alias){
		this.type = type;
		this.expression = expression;
		this.alias = alias;
	}

	public AggregateType getType() {
		return type;
	}

	public void setType(AggregateType type) {
		this.type = type;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
