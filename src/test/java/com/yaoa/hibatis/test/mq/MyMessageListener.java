/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.mq;

import com.yaoa.hibatis.mq.MessageListener;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
public class MyMessageListener implements MessageListener<MyMessage>{

	public void onMessage(MyMessage message) {
		System.out.println(message.getChannel() + ">" + message.getContent());
	}

}
