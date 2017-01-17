package com.yaoa.hibatis.mq;

/**
 * @author cjh
 * @version 1.0
 */
public interface MessageProducer {
	
	/**
	 * 同步发送消息，只要不抛异常就表示成功
	 * @param message 消息
	 */
	void send(Message message);

	/**
	 * 发送异步消息，异步Callback形式
	 * @param message 消息
	 */
	 void sendAsync(Message message);
	 

	/**
	 * 单向发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
	 * @param message 消息
	 */
	 void sendOneway(Message message);
}
