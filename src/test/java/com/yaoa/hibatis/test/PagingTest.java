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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Page;
import com.yaoa.hibatis.query.build.CriterionBuilder;
import com.yaoa.hibatis.test.model.Order;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月22日
 */
public class PagingTest {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test() throws IOException{
		Criterion criterion = CriterionBuilder.create(Order.class)
				.equal("id", "1").orderBy("id desc").page(0, 15).cache().build();
		Page<Object> result = sqlMapper.paging(criterion);
		result = sqlMapper.paging(criterion);
		result = sqlMapper.paging(criterion);
		System.out.println(new ObjectMapper().writeValueAsString(result));
	}
}
