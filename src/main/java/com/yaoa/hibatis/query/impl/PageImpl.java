/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.impl;

import java.util.List;

import com.yaoa.hibatis.query.Page;

/**
 * 
 * 分页结果
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 * @param <T>
 */
public class PageImpl<T> implements  Page<T>{

	private int pageSize;
	
	private int currentPage;
	
	private long total;
	
	private List<T> content;
	
	public PageImpl(int firstResult , int maxResults , long total , List<T> content){
		this.currentPage = (int) Math.ceil(((firstResult + 1d)/ maxResults)); 
		this.pageSize = maxResults;
		this.total = total;
		this.content = content;
	}

	public long getTotal() {
		return total;
	}

	public List<T> getContent() {
		return content;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}
	
	public static void main(String[] args) {
		System.out.println();
	}
}
