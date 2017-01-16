/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.query.build.CriterionBuilder;
import com.yaoa.hibatis.test.model.Order;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月22日
 */
public class AssociationTest {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test() throws IOException{
		long start = System.currentTimeMillis();
		List<Order> orders = sqlMapper.find(CriterionBuilder.create(Order.class).cache().build());
		for (Order order : orders) {
			System.out.println(order.getCustomer().getName());
			System.out.println(order.getDetails().size());
		}
		System.out.println(new ObjectMapper().writeValueAsString(orders));
		long end = System.currentTimeMillis();
		System.out.println((end - start) + "毫秒");
	}
}
