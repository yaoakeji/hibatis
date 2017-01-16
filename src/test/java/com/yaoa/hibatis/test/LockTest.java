/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yaoa.hibatis.SqlMapperTemplate;
import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockManager;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
public class LockTest {
	
	private SqlMapperTemplate sqlMapper;
	
	@Before
	public void init(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		sqlMapper =  context.getBean(SqlMapperTemplate.class);
	}

	@Test
	public void test() throws InterruptedException {
		Lock lock = LockManager.getLock("1");
		lock.lock();
		System.out.println("开始锁");
		Thread thread = new Thread(new Runnable() {

			public void run() {
				Lock lock = LockManager.getLock( "1");
				lock.lock();
				System.out.println("开始锁2");
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("结束锁2");
				lock.unlock();
			}
		});
		thread.start();
		Thread.sleep(10 * 1000);
		lock.unlock();
		System.out.println("结束锁");
		thread.join();
	}
}
