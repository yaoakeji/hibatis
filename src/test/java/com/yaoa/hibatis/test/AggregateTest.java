/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.build.AggregateBuilder;
import com.yaoa.hibatis.query.build.CriterionBuilder;
import com.yaoa.hibatis.test.model.Customer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public class AggregateTest {

	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test(){
		Criterion criterion = CriterionBuilder.create(Customer.class).build();
		Aggregate aggregate = AggregateBuilder.create(Customer.class)
				.count("1", "name1")
//				.field("name", "name2")
//				.max("1","count")
//				.where(criterion)
//				.groupBy("name")
				.build();
		Object result = sqlMapper.aggregate(aggregate, String.class);
		System.out.println(result);
	}
}
