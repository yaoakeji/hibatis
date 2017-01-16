/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.test.model.Customer;
import com.yaoa.hibatis.test.model.Test;
import com.yaoa.hibatis.transaction.TransactionCacheContextHolder;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月25日
 */
@Service
public class CustomerService{

	@Autowired
	private SqlMapperTemplate sqlMapper;
	
	@Transactional
	public void testLock(){
		System.out.println("开始锁");
		Customer customer  = sqlMapper.findById(Customer.class, 1 , LockMode.UPGRADE);
		Thread thread = new Thread(new Runnable() {

			public void run() {
				System.out.println("开始锁2");
				Customer customer  = sqlMapper.findById(Customer.class, 1 , LockMode.UPGRADE);
				System.out.println("结束锁2");
			}
		});
		thread.start();
		try {
			Thread.sleep(5 * 1000);
			System.out.println("结束锁");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
