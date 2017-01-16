package com.yaoa.hibatis.test.model;

import com.yaoa.hibatis.annotation.Entity;
import com.yaoa.hibatis.annotation.Id;
import com.yaoa.hibatis.annotation.Table;
import com.yaoa.hibatis.annotation.Transient;

@Entity(cacheable = false)
@Table(name = "tb_order_detail")
public class OrderDetail {

	@Id
	private int id;
	
	private int orderId;
	
	private String goods;
	
	@Transient
	private double price;
	
	@Transient
	private int qty;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}
