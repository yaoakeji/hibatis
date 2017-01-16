/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.serializer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月29日
 */
public interface Serializer<T> {

	public byte[] serialize(T obj) ;
	
	public T deserialize(byte[] bytes) ;
}
