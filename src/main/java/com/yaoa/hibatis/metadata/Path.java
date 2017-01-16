/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月22日
 */
public interface Path {
	
	public String getName();
	
	public String getPropertyName();

	public String getColumn();
	
	public String getMapping();
}
