/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.lock.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockProvider;
import com.yaoa.hibatis.redis.connection.RedisConnection;
import com.yaoa.hibatis.redis.connection.RedisConnectionFactory;
import com.yaoa.hibatis.serializer.Serializer;
import com.yaoa.hibatis.serializer.StringSerializer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
public class RedisLockProvider implements LockProvider {

	private RedisConnectionFactory connectionFactory;

	private int timeout = 120;

	private Serializer<String> keySerializer = new StringSerializer<String>();

	private int expireSeconds = 10; // 10秒过期

	private static Map<String, Long> lockedKeys = new ConcurrentHashMap<String, Long>(); // 已锁的键

	private static Thread liveThread; // 续命线程

	private final static Log logger = LogFactory.getLog(RedisLockProvider.class);

	public RedisLockProvider(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	private synchronized void checkStartThread() {
		if (liveThread != null) {
			return;
		}
		liveThread = new Thread(new Runnable() {
			public void run() {
				if (liveThread.isInterrupted()) {
					return;
				}
				while (true) {
					try {
						for (Entry<String, Long> entry : lockedKeys.entrySet()) {
							Long time = entry.getValue();
							if (time != null && time <= System.currentTimeMillis()) {
								String key = entry.getKey();
								RedisConnection connection = connectionFactory.getConnnection();
								try {
									byte[] keyBytes = keySerializer.serialize(key);
									connection.expire(keyBytes, TimeUnit.SECONDS, expireSeconds);
								} finally {
									connection.close();
								}
								entry.setValue(System.currentTimeMillis() + (expireSeconds - 2) * 1000); // 下一次到期时间
							}
						}
					} catch (Exception e) {
						logger.error("RedisLock续命出错", e);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		liveThread.start();
	}

	public boolean addLock(String key) {
		if (liveThread == null) {
			checkStartThread();
		}
		boolean success;
		Long time = System.currentTimeMillis();
		byte[] keyBytes = keySerializer.serialize(key);
		byte[] valueBytes = keySerializer.serialize(Thread.currentThread().getName());
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			success = connection.setnx(keyBytes, valueBytes, TimeUnit.SECONDS, expireSeconds);
		} finally {
			connection.close();
		}
		if (success) {
			lockedKeys.put(key, time + (expireSeconds - 2) * 1000); // 提前2秒到期
		}
		return success;
	}

	public void removeLock(String key) {
		lockedKeys.remove(key);
		byte[] keyBytes = keySerializer.serialize(key);
		RedisConnection connection = connectionFactory.getConnnection();
		try {
			connection.del(keyBytes);
		} finally {
			connection.close();
		}
	}

	public Lock getLock(String key) {
		return new RedisLock(key, this);
	}

	public Serializer<String> getKeySerializer() {
		return keySerializer;
	}

	public void setKeySerializer(Serializer<String> keySerializer) {
		this.keySerializer = keySerializer;
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

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public void shutdown() {
		liveThread.interrupt();
	}

}
