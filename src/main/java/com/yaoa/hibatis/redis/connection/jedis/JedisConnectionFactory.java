/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.redis.connection.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.yaoa.hibatis.redis.connection.RedisConnection;
import com.yaoa.hibatis.redis.connection.RedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月29日
 */
public class JedisConnectionFactory implements RedisConnectionFactory , InitializingBean , DisposableBean{

	private GenericObjectPoolConfig poolConfig;
	
	private JedisPool jedisPool;
	
	private String host;
	
	private int port;
	
	private int timeout;
	
	private String password;
	
	private int database = 0;
	
	public JedisConnectionFactory(){
		
	}
	
	public JedisConnectionFactory(GenericObjectPoolConfig poolConfig){
		this.poolConfig = poolConfig;
	}

	public RedisConnection getConnnection() {
		Jedis jedis;	
		if(jedisPool == null){
			jedis = new Jedis(host, port, timeout, timeout);
			if(!StringUtils.isEmpty(password)) {
				jedis.auth(password);
			}
		}else{
			jedis = jedisPool.getResource();
		}
		return new JedisConnection(jedis);
	}
	
	public void destroy() throws Exception {
		if(jedisPool != null){
			jedisPool.destroy();
		}
	}

	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isEmpty(password)){
			password = null;
		}
		if(poolConfig != null){
			jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
		}
	}
	

	public GenericObjectPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}
}
