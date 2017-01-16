package com.yaoa.hibatis.test;

import java.io.IOException;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.test.model.Customer;

public class HibatisTest {

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
		List<Customer> list = sqlMapper.findAll(Customer.class);
		list = sqlMapper.findAll(Customer.class);
		list = sqlMapper.findAll(Customer.class);
		list = sqlMapper.findAll(Customer.class);
		Customer customer = list.get(0);
		customer.setName("test" + System.currentTimeMillis());
		sqlMapper.update(customer);
//		sqlMapper.delete(customer);
		String json = new ObjectMapper().writeValueAsString(list);
		System.out.println(json);
	}
}
