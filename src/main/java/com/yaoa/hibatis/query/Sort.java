/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

/**
 * 
 * 排序依据
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class Sort {
	
	private Direction direction;
	
	private String name;

	public Sort(Direction direction , String name){
		this.direction = direction;
		this.name = name;
	}
	
	public static Sort parse(String order){
		String[] strArr = order.split(" ", 2);
		String name = strArr[0];
		Direction direction = Direction.ASC;
		if(strArr.length > 1 && "DESC".equalsIgnoreCase(strArr[1])){
			direction =  Direction.DESC ;
		}
		return new Sort(direction, name);
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
