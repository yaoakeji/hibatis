/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.mq;

import org.springframework.util.StringUtils;

/**
 * 消息实体
 * @author kingsy.lin
 * @version 1.0 , 2016年12月28日
 */
public abstract class Message {
	
	/**
	 * 消息唯一标识， 防止消息重复投递
	 */
	private String key;
	
	/**
	 * 消息投递时间 ，为空则为即时投递
	 */
	private Long deliverTime;

	public String getKey() {
		return StringUtils.isEmpty(key) ? "" : key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Long deliverTime) {
		this.deliverTime = deliverTime;
	}
	
	

}
