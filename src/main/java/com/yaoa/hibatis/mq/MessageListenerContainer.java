/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.mq;

/**
 * @Description 消息监听器容器
 * @author cjh
 * @version 1.0
 * @date：2016年12月23日 上午10:08:22
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
