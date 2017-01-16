/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import java.io.IOException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.test.model.Customer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
public class RefreshTest {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		SqlSessionFactory sqlSessionFactory =  context.getBean(SqlSessionFactory.class);
		sqlMapper = new SqlMapperTemplate(sqlSessionFactory);
	}

	@Test
	public void test() throws IOException{
		Customer customer1 = sqlMapper.findById(Customer.class, 1);
		Customer customer2 = sqlMapper.findById(Customer.class, 1);
		
		String name = "test" + System.currentTimeMillis();
		
		customer2.setName(name);
		sqlMapper.update(customer2);
		System.out.println(name);
		System.out.println(customer2.getName());
		
		sqlMapper.refresh(customer1, LockMode.UPGRADE);
		
		System.out.println(customer1.getName());
	}
}
