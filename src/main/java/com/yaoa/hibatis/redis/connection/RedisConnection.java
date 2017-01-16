/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.redis.connection;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月29日
 */
public interface RedisConnection {

	public byte[] get(byte[] key);
	
	public boolean exists(byte[] key) ;
	
	public void set(byte[] key , byte[] value) ;
	
	public void set(byte[] key , byte[] value , TimeUnit unit , long duration);
	
	public boolean setnx(byte[] key , byte[] value);
	
	public boolean setnx(byte[] key , byte[] value , TimeUnit unit , long duration);
	
	public long expire(byte[] key ,  TimeUnit unit , long duration) ;
	
	public long del(byte[]... pattern) ;
	
	public Set<byte[]> keys(byte[] pattern) ;
	
	public long rpush(byte[] key , byte[] value);
	
	public byte[] lpop(byte[] key);
	
	public void close();
}
