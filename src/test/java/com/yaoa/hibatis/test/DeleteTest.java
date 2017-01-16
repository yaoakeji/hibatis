/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.query.build.CriterionBuilder;
import com.yaoa.hibatis.test.model.Customer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
public class DeleteTest {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test() throws IOException{
		sqlMapper.delete(CriterionBuilder.create(Customer.class)
				.equal("id", 4).build());
//		for (int id = 8; id <= 49; id++) {
//			Customer customer = sqlMapper.findById(Customer.class, id);
//			if(customer == null){
//				continue;
//			}
//			sqlMapper.delete(Customer.class , customer);
//		}
	}
}
