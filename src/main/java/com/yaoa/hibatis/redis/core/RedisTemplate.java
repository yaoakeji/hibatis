/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.redis.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.yaoa.hibatis.redis.connection.RedisConnection;
import com.yaoa.hibatis.redis.connection.RedisConnectionFactory;
import com.yaoa.hibatis.serializer.KryoSerializer;
import com.yaoa.hibatis.serializer.Serializer;
import com.yaoa.hibatis.serializer.StringSerializer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
public class RedisTemplate<K, V> {

	private RedisConnectionFactory connectionFactory;

	private Serializer<K> keySerializer = new StringSerializer<K>();

	private Serializer<V> valueSerializer = new KryoSerializer<V>();
	
	public RedisTemplate(){
		
	}
	
	public RedisTemplate(RedisConnectionFactory connectionFactory){
		this.connectionFactory = connectionFactory;
	}

	public V get(K key) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			byte[] valueBytes = connection.get(keyBytes);
			if (valueBytes == null) {
				return null;
			} else {
				return valueSerializer.deserialize(valueBytes);
			}
		} finally {
			connection.close();
		}
	}

	public void set(K key, V value) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			byte[] valueBytes = valueSerializer.serialize(value);
			connection.set(keyBytes, valueBytes);
		} finally {
			connection.close();
		}
	}
	
	public void set(K key, V value, TimeUnit unit , long duration) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			byte[] valueBytes = valueSerializer.serialize(value);
			connection.set(keyBytes, valueBytes , unit , duration);
		} finally {
			connection.close();
		}
	}

	public boolean setnx(K key, V value) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			byte[] valueBytes = valueSerializer.serialize(value);
			return connection.setnx(keyBytes, valueBytes);
		} finally {
			connection.close();
		}
	}

	public long expire(K key, TimeUnit unit , long duration) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			return connection.expire(keyBytes, unit , duration);
		} finally {
			connection.close();
		}
	}

	public boolean exists(K key) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			return connection.exists(keyBytes);
		} finally {
			connection.close();
		}
	}

	public long del(K key) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] keyBytes = keySerializer.serialize(key);
			return connection.del(keyBytes);
		} finally {
			connection.close();
		}
	}

	public Set<K> keys(K pattern) {
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			byte[] patternBytes = keySerializer.serialize(pattern);
			Set<byte[]> keyByteSet = connection.keys(patternBytes);
			Set<K> keySet = new HashSet<K>(keyByteSet.size());
			for (byte[] keyBytes : keyByteSet) {
				K key = keySerializer.deserialize(keyBytes);
				keySet.add(key);
			}
			return keySet;
		} finally {
			connection.close();
		}
	}

	public RedisConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Serializer<K> getKeySerializer() {
		return keySerializer;
	}

	public void setKeySerializer(Serializer<K> keySerializer) {
		this.keySerializer = keySerializer;
	}

	public Serializer<V> getValueSerializer() {
		return valueSerializer;
	}

	public void setValueSerializer(Serializer<V> valueSerializer) {
		this.valueSerializer = valueSerializer;
	}
}
