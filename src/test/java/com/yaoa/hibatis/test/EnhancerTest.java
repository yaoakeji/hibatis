/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.test.model.Customer;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月19日
 */
public class EnhancerTest {
	
	public static void main(String[] args) throws Exception {
		Customer cust = new Customer();
		cust.setId(1);
		cust.setNumber("10001");
		cust.setName("测试");
		Customer entity = (Customer)EntityEnhancer.enhance(cust);
//		entity.setId(2);
//		entity.setNumber("10002");
		entity.getClass().getMethod("setNumber", String.class)
		.invoke(entity, "10003");
		System.out.println(entity.hashCode() + "," + cust.hashCode());
		
		entity.setNumber(null);
		
		System.out.println(EntityEnhancer.getVersion((HibatisEntity)entity));
		System.out.println(EntityEnhancer.getState((HibatisEntity)entity));
		System.out.println(EntityEnhancer.getEntityType(entity));
		System.out.println(EntityEnhancer.getPropertyChanges((HibatisEntity)entity));
		
		String json = new ObjectMapper().writeValueAsString(entity);
		System.err.println(json);
		
		System.out.println(entity.getNumber());
	}
}
