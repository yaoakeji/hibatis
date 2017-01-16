/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

/**
 * 连接符
 * @author kingsy.lin
 *
 */
public enum Connective {

	/**
	 * 并且
	 */
	AND("AND"),
	/**
	 * 或者
	 */
	OR("OR");
	
	private String symbol;
	
	Connective(String symbol){
		this.symbol = symbol;
	}
	
	
	@Override
	public String toString() {
		return symbol;
	}

}
