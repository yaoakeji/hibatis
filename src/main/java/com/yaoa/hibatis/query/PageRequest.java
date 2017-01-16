/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

/**
 * 
 * 分页请求
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class PageRequest {
	
	private int firstResult;
	
	private int maxResults;
	
	public PageRequest(int firstResult , int maxResults){
		this.firstResult = firstResult;
		this.maxResults = maxResults;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

}
