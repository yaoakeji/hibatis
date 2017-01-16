/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.test.model.Order;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月22日
 */
public class FindByIdsTest {

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
		Serializable[] idArray = new Serializable[]{ 1, 2, 3};
		List<Order> orders = sqlMapper.findByIds(Order.class, idArray , LockMode.UPGRADE);
		System.out.println(new ObjectMapper().writeValueAsString(orders));
	}
}
