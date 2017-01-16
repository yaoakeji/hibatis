/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.serializer;


import java.nio.charset.Charset;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月28日
 */
@SuppressWarnings("unchecked")
public class StringSerializer<T> implements Serializer<T> {
	
	private Charset charset = Charset.forName("utf-8");
	
	public byte[] serialize(T t)  {
		return t.toString().getBytes(charset);
	}

	public T deserialize(byte[] bytes)  {
		return (T)new String(bytes, charset);
	}
}