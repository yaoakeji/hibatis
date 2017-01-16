/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.test.service.CustomerService;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
public class EntityLockTest {

	private SqlMapperTemplate sqlMapper;
	
	private CustomerService customerService;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
		customerService = context.getBean(CustomerService.class);
	}

	@Test
	public void test() throws Exception{
		customerService.testLock();
		Thread.sleep(2 * 1000);
		System.out.println("完成");
	}
	
	public static void main(String[] args) throws InterruptedException {
		
	}
}
