/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.yaoa.hibatis.cache.CacheCollection;

/**
 * 
 * 缓存信息
 * @author kingsy.lin
 * @version 1.0 , 2017年1月13日
 */
class CacheCollectionInfo implements Serializable {

	private static final long serialVersionUID = 3195046509908222958L;

	public List<String> keys;

	private long total;
	
	public CacheCollectionInfo(){
		
	}

	public CacheCollectionInfo(CacheCollection<?> collection) {
		this.keys = new ArrayList<String>(collection.getList().size());
		this.total = collection.getTotal();
	}

	public List<String> getKeys() {
		return keys;
	}

	public long getTotal() {
		return total;
	}
}
