/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.redis.connection.jedis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.yaoa.hibatis.redis.connection.RedisConnection;

import redis.clients.jedis.Jedis;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月29日
 */
public class JedisConnection implements RedisConnection{

	private Jedis jedis;
	
	public JedisConnection(Jedis jedis){
		this.jedis = jedis;
	}
	
	public byte[] get(byte[] key) {
		return jedis.get(key);
	}
	
	public boolean exists(byte[] keys) {
		return jedis.exists(keys);
	}
	
	public void set(byte[] key , byte[] value) {
		 jedis.set(key, value);
	}
	
	public void set(byte[] key , byte[] value , TimeUnit unit , long duration){
		 long milliseconds = unit.toMillis(duration);
		 jedis.psetex(key, milliseconds, value);
	}
	
	public boolean setnx(byte[] key , byte[] value ) {
		return jedis.setnx(key, value) > 0;
	}
	
	public boolean setnx(byte[] key , byte[] value , TimeUnit unit , long duration) {
		return jedis.set(key, value, "NX".getBytes(), "PX".getBytes(), unit.toMillis(duration)) != null;
	}
	
	public long expire(byte[] key ,  TimeUnit unit , long duration) {
		return jedis.pexpire(key, unit.toMillis(duration));
	}
	
	public long del(byte[]... pattern) {
		return jedis.del(pattern);
	}
	
	public Set<byte[]> keys(byte[] pattern) {
		return jedis.keys(pattern);
	}

	public long rpush(byte[] key, byte[] value) {
		return jedis.rpush(key, value);
	}

	public byte[] lpop(byte[] key) {
		return jedis.lpop(key);
	}

	public void close() {
		jedis.close();
	}

	public Jedis getJedis() {
		return jedis;
	}
}
