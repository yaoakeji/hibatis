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
import com.yaoa.hibatis.test.model.Customer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
public class InsertTest {

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
		Customer customer = new Customer();
		customer.setNumber("10001");
		customer.setName("test");
		sqlMapper.insert(customer);
		
		//修改直接更新，看是否执行SQL
		sqlMapper.update(customer);
		
		//修改属性后更新，查看执行SQL
		customer.setName("test2");
		sqlMapper.update(customer);
		
		System.out.println(customer.getId());
	}
}
