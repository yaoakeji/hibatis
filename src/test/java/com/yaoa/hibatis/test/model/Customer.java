package com.yaoa.hibatis.test.model;

import java.io.Serializable;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.annotation.Id;
import com.yaoa.hibatis.annotation.Table;

@Entity(cacheable = true)
@Table(name = "tb_customer")
public class Customer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -621894811880272511L;

	@Id
	private int id;

	private String number;
	
	private String name;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
