/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache;

import java.util.List;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2017年1月13日
 */
public class CacheCollection<T> {

	private long total;
	
	private List<T> list;
	
	public CacheCollection(List<T> list , long total){
		this.list = list;
		this.total = total;
	}

	public long getTotal() {
		return total;
	}

	public List<T> getList() {
		return list;
	}
}
