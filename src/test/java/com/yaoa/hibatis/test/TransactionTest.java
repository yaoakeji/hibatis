/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.test.service.Service1;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
public class TransactionTest {

	@Autowired
	private Service1 service1;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		service1 =  context.getBean(Service1.class);
	}

	@Test
	public void test() throws IOException{
		service1.insert();
	}
	
}
