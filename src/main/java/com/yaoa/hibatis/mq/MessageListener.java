/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.mq;

/**
 * @Description 消息监听器
 * @author cjh
 * @version 1.0
 * @date：2016年12月23日 上午10:08:22
 */
public interface MessageListener<T extends Message> {
	
	/**
	 * 接收并处理订阅消息
	 * @param channel
	 * @param message
	 */
	void onMessage(T message);

}

