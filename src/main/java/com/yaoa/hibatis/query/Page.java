/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

import java.util.List;

/**
 * 
 * 分页结果
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 * @param <T> 类型
 */
public interface Page<T> {
	
	public int getPageSize();
	
	public int getCurrentPage();
	
	public long getTotal();

	public List<T> getContent();
}
