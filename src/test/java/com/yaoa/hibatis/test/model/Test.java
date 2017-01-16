/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.model;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.annotation.Id;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月26日
 */
@Entity
public class Test {


	@Id(useGeneratedKeys = false)
	private long a;

	public long getA() {
		return a;
	}

	public void setA(long a) {
		this.a = a;
	}
	
}
