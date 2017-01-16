/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.mq;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.mq.MessageProducer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
public class MessageListenerTest {
	
	private ClassPathXmlApplicationContext context;
	
	private MessageProducer messageProducer;

	@Before
	public void init(){
		context = new ClassPathXmlApplicationContext("spring.xml");
	}

	@Test
	public void test() throws IOException{
		for (int i = 0; i < 100; i++) {
			messageProducer.send(new MyMessage("啊大饭店" + i));
		}
		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		context.destroy();
	}
}
