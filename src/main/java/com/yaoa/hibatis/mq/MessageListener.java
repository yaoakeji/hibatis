/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.mq;

/**
 * @author cjh
 * @version 1.0
 */
public interface MessageListener<T extends Message> {
	
	/**
	 * 接收并处理订阅消息
	 * @param message 消息
	 */
	void onMessage(T message);

}

