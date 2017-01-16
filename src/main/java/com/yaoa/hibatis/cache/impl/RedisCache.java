/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.cache.impl;

import java.util.concurrent.TimeUnit;

import com.yaoa.hibatis.cache.Cache;
import com.yaoa.hibatis.redis.connection.RedisConnection;
import com.yaoa.hibatis.redis.connection.RedisConnectionFactory;
import com.yaoa.hibatis.serializer.KryoSerializer;
import com.yaoa.hibatis.serializer.Serializer;
import com.yaoa.hibatis.serializer.StringSerializer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月28日
 */
public class RedisCache implements Cache {
	
	private RedisConnectionFactory connectionFactory;
	
	private Serializer<String> keySerializer = new StringSerializer<String>();
	
	private Serializer<Object> valueSerializer = new KryoSerializer<Object>();
	
	private int timeout = 7200; //默认2个小时超时
	
	public RedisCache(RedisConnectionFactory connectionFactory){
		this.connectionFactory = connectionFactory;
	}

	public void put(String key, Object value) {
		byte[] keyBytes = keySerializer.serialize(key);
		byte[] valueBytes = valueSerializer.serialize(value);
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			connection.set(keyBytes, valueBytes, TimeUnit.SECONDS, timeout);
		} finally {
			connection.close();
		}
	}

	public Object get(String key) {
		byte[] valueBytes;
		byte[] keyBytes = keySerializer.serialize(key);
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			valueBytes = connection.get(keyBytes);
		}finally{
			connection.close();
		}
		if(valueBytes == null){
			return null;
		}else{
			return valueSerializer.deserialize(valueBytes);
		}
	}

	public void remove(String key) {
		byte[] keyBytes = keySerializer.serialize(key);
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			connection.del(keyBytes);
		} finally {
			connection.close();
		}
	}
	
	public boolean isMemory() {
		return false;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public RedisConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public Serializer<String> getKeySerializer() {
		return keySerializer;
	}

	public void setKeySerializer(Serializer<String> keySerializer) {
		this.keySerializer = keySerializer;
	}

	public Serializer<Object> getValueSerializer() {
		return valueSerializer;
	}

	public void setValueSerializer(Serializer<Object> valueSerializer) {
		this.valueSerializer = valueSerializer;
	}

}
