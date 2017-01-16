package com.yaoa.hibatis.mq.aliware;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.yaoa.hibatis.mq.MessageChannel;
import com.yaoa.hibatis.mq.MessageProducer;
import com.yaoa.hibatis.serializer.Serializer;

/**
 * @Description 消息生产者
 * @author cjh
 * @version 1.0
 * @date：2016年12月22日 下午9:22:14
 */
public class AliMessageProducer implements InitializingBean , DisposableBean, MessageProducer{
	
	public final static long MSG_MAX_STORE_TIME = 39L * 24L * 60L * 60L * 1000L;
	
	private String topic;
	
	private String producerId;
	
	private String accessKey;
	
	private String secretKey;
	
	private Producer producer;
	
	private Serializer<Object> serializer;
	
	private static final Log logger = LogFactory.getLog(AliMessageProducer.class);
    
    /**
	 * 同步发送消息，只要不抛异常就表示成功
	 * @param key 消息唯一标识， 防重复
	 * @param message 消息
	 */
    public void send(com.yaoa.hibatis.mq.Message message){
    	Message onsMsg = createONSMessage(message);
    	producer.send(onsMsg);
    }
    
    /**
	 * 单向发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
	 * @param key 消息唯一标识， 防重复
	 * @param message 消息
	 */
    public void sendOneway(com.yaoa.hibatis.mq.Message message){
    	Message onsMsg = createONSMessage(message);
    	producer.sendOneway(onsMsg);
    }

    
    /**
   	 * 发送异步消息，异步Callback形式
   	 * @param channel 发送频道
   	 * @param key 消息唯一标识， 防重复
   	 * @param message
   	 */
    public void sendAsync(com.yaoa.hibatis.mq.Message message){
    	Message onsMsg = createONSMessage(message);
		producer.sendAsync(onsMsg, new SendCallback() {
			
			public void onSuccess(SendResult sendResult) {
				
			}
			
			public void onException(OnExceptionContext context) {
				logger.error("发送消息[" + context.getMessageId() +"] 失败" , context.getException());
			}
		});
    }

    public void destroy() throws Exception {
		if (this.producer != null) {
			this.producer.shutdown();
		}
	}

	public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put("ProducerId", this.getProducerId());
        properties.put("AccessKey", this.accessKey);
        properties.put("SecretKey", this.secretKey);
		this.producer = ONSFactory.createProducer(properties);
        this.producer.start();
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
	
	/**
	 * 转化为ONS消息
	 * @param msg
	 * @param deliverTime
	 */
	private Message createONSMessage(com.yaoa.hibatis.mq.Message message){
		Long deliverTime = message.getDeliverTime();
		String channel = getMessageChannel(message.getClass());
    	if(deliverTime != null){
    		// 判断是否
    		long currMillis = System.currentTimeMillis();
    		if(deliverTime - currMillis > MSG_MAX_STORE_TIME){
    			deliverTime = currMillis + MSG_MAX_STORE_TIME;
    		}else{
        		message.setDeliverTime(null); //设置为null,不跟随序列化
    		}
    	}
    	String key = message.getKey();
    	message.setKey(null); //设置为null,不跟随序列化
    	Message onsMsg = new Message(topic, channel, key, serializer.serialize(message));
    	if(deliverTime != null){
    		onsMsg.setStartDeliverTime(deliverTime);
    	}
    	return onsMsg;
	}
	
	public String getProducerId() {
		return producerId;
	}


	public void setProducerId(String producerId) {
		this.producerId = producerId;
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


	public Serializer<Object> getSerializer() {
		return serializer;
	}


	public void setSerializer(Serializer<Object> serializer) {
		this.serializer = serializer;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public boolean isStarted() {
		return producer.isStarted();
	}
	
	public boolean isClosed() {
		return producer.isClosed();
	}

	public void start() {
		if (!producer.isStarted()) {
			this.producer.start();
		}
	} 
	
	public void shutdown() {
		if (!producer.isClosed()) {
			this.producer.shutdown();
		}
	}
	
	
	
}
