/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.mq;

/**
 * @author cjh
 * @version 1.0
 */
public interface MessageListenerContainer {
	
	/**
	 * 启动监听容器
	 */
	void start();
	
	/**
	 * 关闭监听容器
	 */
	void shutdown();
	
	
}
