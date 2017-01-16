/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.test.model.Order;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年11月16日
 */
public class InsertTest2 {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test() throws Exception {
		Order order = new Order();
		order.setCustId("1");
		order.setNumber("adfafd");
		sqlMapper.insert(order);
		System.out.println(order.getCustomer().getNumber());	
		System.out.println(order.getDetails().size());
	}
}
