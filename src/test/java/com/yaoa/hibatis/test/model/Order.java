package com.yaoa.hibatis.test.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.yaoa.hibatis.annotation.Association;
import com.yaoa.hibatis.annotation.Collection;
import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.annotation.Id;
import com.yaoa.hibatis.annotation.Table;

@Entity(cacheable = true , version = "1.1")
@Table(name = "tb_order")
public class Order implements Serializable{

	private static final long serialVersionUID = 375587337670007717L;

	@Id
	private Integer id;
	
	@NotNull
	private String number;
	
	private String custId;
	
	@Association(property = "custId" , lazy = true)
	private Customer customer;
	
	@Collection(reference = "orderId"  , in = true , orderBy = "id desc" , lazy = false)
	private List<OrderDetail> details; 
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
	
}
