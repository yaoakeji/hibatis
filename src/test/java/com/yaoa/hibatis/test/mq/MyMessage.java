/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.test.mq;

import com.yaoa.hibatis.mq.Message;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月30日
 */
public class MyMessage extends Message{

	private String content;
	
	public MyMessage(){
		
	}

	public MyMessage(String content) {
		super();
		this.content = content;
	}
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getChannel() {
		return "test";
	}
}
