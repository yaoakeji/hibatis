package com.yaoa.hibatis.mq.aliware;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.yaoa.hibatis.mq.Message;
import com.yaoa.hibatis.mq.MessageChannel;
import com.yaoa.hibatis.mq.MessageListener;
import com.yaoa.hibatis.mq.MessageListenerContainer;
import com.yaoa.hibatis.serializer.Serializer;

/**
 * @author cjh
 * @version 1.0
 */
public class AliMessageListenerContainer implements InitializingBean, DisposableBean , ApplicationContextAware, MessageListenerContainer {

	private final static Log logger = LogFactory.getLog(AliMessageListenerContainer.class);

	private String topic;

	private String consumerId;
	
	private String accessKey;

	private String secretKey;

	private int consumeThreadNums = 5;

	private Consumer consumer;

	private AliMessageProducer producer; //用来发送延迟消息的生产者

	private Serializer<Message> serializer;

	private Map<String,List<MessageListener<Message>>> listenerMap;

	public void destroy() throws Exception {
		this.shutdown();
	}
	
	public void start() {
		if (!consumer.isStarted()) {
			this.consumer.start();
		}
	}

	public void shutdown() {
		if (!consumer.isClosed()) {
			consumer.shutdown();
		}
	}

	public void afterPropertiesSet() throws Exception {
		Properties properties = new Properties();
		properties.put("ConsumerId", this.consumerId);
		properties.put("AccessKey", this.accessKey);
		properties.put("SecretKey", this.secretKey);
		properties.put("ConsumeThreadNums", this.consumeThreadNums);
		this.consumer = ONSFactory.createConsumer(properties);
		StringBuffer subExp = new StringBuffer();
		for (String channel : listenerMap.keySet()) {
			subExp.append(" || ").append(channel);
		}
		this.consumer.subscribe(topic, subExp.delete(0, 4).toString(), new AliMessageListener());
		this.consumer.start();
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(listenerMap == null){
			listenerMap = new HashMap<String, List<MessageListener<Message>>>();
		}
		producer = applicationContext.getBean(AliMessageProducer.class);
		Map<String, MessageListener> map = applicationContext.getBeansOfType(MessageListener.class);
		for (MessageListener listener : map.values()) {
			ParameterizedType type = (ParameterizedType) listener.getClass().getGenericInterfaces()[0];
			Class<Message> clazz = (Class<Message>) type.getActualTypeArguments()[0];
			String channel = getMessageChannel(clazz);
			List<MessageListener<Message>> listeners = listenerMap.get(channel);
			if(listeners == null){
				listeners = new ArrayList<MessageListener<Message>>();
			}
			listeners.add(listener);
			listenerMap.put(channel, listeners);
		}
	}
	
	private class AliMessageListener implements com.aliyun.openservices.ons.api.MessageListener {
		public Action consume(com.aliyun.openservices.ons.api.Message onsMsg, ConsumeContext context) {
			try {
				Message message = serializer.deserialize(onsMsg.getBody());
				if(message.getDeliverTime() != null){
					producer.sendAsync(message);
				}else{
					message.setKey(onsMsg.getKey());
					for (MessageListener<Message> listener : listenerMap.get(onsMsg.getTag())) {
						listener.onMessage(message);
					}
				}
				return Action.CommitMessage;
			} catch (Exception e) {
				if(e instanceof ClassCastException){
					logger.error("消息类型不匹配：" + e.getMessage());
					return Action.CommitMessage;
				}else{
					logger.error("消息处理异常：", e);
					return Action.ReconsumeLater;
				}
			}
		}
	}
	
	/**
	 * 根据消息体类型获取消息频道
	 * @param clazz 消息体类型
	 * @return
	 */
	private String getMessageChannel(Class<? extends com.yaoa.hibatis.mq.Message> clazz){
		MessageChannel channel = clazz.getAnnotation(MessageChannel.class);
		return channel == null ? clazz.getName() : channel.value();
	}
	
	public String getConsumerId() {
		return consumerId;
	}
	
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Serializer<Message> getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer<Message> serializer) {
		this.serializer = serializer;
	}
	
	public int getConsumeThreadNums() {
		return consumeThreadNums;
	}
	
	public void setConsumeThreadNums(int consumeThreadNums) {
		this.consumeThreadNums = consumeThreadNums;
	}
	
}
