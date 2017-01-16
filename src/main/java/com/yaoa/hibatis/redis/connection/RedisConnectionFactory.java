/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.redis.connection;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月29日
 */
public interface RedisConnectionFactory {

	public RedisConnection getConnnection();
}
