/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

/**
 * 
 * 操作符
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public enum Operator {

	/**
	 * 相等
	 */
	EQ ("={0}"),
	/**
	 * 大于
	 */
	GT (">{0}"), 
	/**
	 * 大于等于
	 */
	GE (">={0}"),
	/**
	 * 小于
	 */
	LT ("<{0}"), 
	/**
	 * 小于等于
	 */
	LE("<={0}"), 
	/**
	 * 不等于
	 */
	NQ ("!={0}"),
	/**
	 * 通配
	 */
	LIKE(" LIKE {0}"),
	/**
	 * 在范围之内
	 */
	IN(" IN ({0...n})"),
	/**
	 * 不在范围之内
	 */
	NOT_IN(" NOT IN ({0...n})"),
	/**
	 * 是否为空
	 */
	IS_NULL(" IS NULL"),
	/**
	 * 是否不为空
	 */
	NOT_NULL(" IS NOT NULL"),
	/**
	 * 在...之间
	 */
	BETWEEN(" BETWEEN {0} and {1}"),
	/**
	 * sql语句
	 */
	NONE(" {0}")
	;
	
	private String expression;
	
	Operator(String expression){
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}
	
	public String render(Object[] values){
		String str = expression;
		if(str.contains("{0}")){
			str = str.replace("{0}", values[0] == null ? "null" : values[0].toString());
		}
		if(str.contains("{1}")){
			str = str.replace("{1}", values[1] == null ? "null" : values[1].toString());
		}
		if(str.contains("{0...n}")){
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < values.length; i++) {
				if(i > 0){
					b.append(",");
				}
				b.append(values[i]);
			}
			str = str.replace("{0...n}", b.toString());
		}
		return str;
	}

//	public static void main(String[] args) {
//		System.out.println(Operator.IN.parse(new String[]{"1","2"}));
//	}
}
