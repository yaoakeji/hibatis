package com.yaoa.hibatis.test.kryo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.yaoa.hibatis.serializer.KryoSerializer;
import com.yaoa.hibatis.test.model.Customer;
import com.yaoa.hibatis.test.model.Order;

/**
 * @Description TODO
 * @author cjh
 * @version 1.0
 * @date：2016年12月16日 下午3:11:24
 */
public class KryoFactoryTest {
	
	/**
	 * 测试jdk 和 kryo 序列化 产生的字节数组的大小（缩小大概3,4倍）
	 * @throws IOException
	 */
	@Test
	public void testSerializerSzie() throws IOException{
		Order order = new Order();
		order.setCustId("123");
		Customer customer = new Customer();
		customer.setId(123);
		customer.setName("sagdfsgfdsgdsfgf");
		customer.setNumber("343e42343523423");
		order.setCustomer(customer);
		KryoSerializer<Order> ser = new KryoSerializer<Order>();
		System.out.println("kryo serializer size： "  + ser.serialize(order).length);//112
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(out);
	    oo.writeObject(order);
	    out.toByteArray();
	    System.out.println("JDK serializer size： "  + out.toByteArray().length);//340
	}
	
	/**
	 * 测试jdk 和 kryo 序列化时间对比 （序列化速度相当快， 基本是十几倍的速度）
	 * new Kryo() 是相当耗时， 大概要消耗300多毫秒, 强烈建议KryoFactory
	 * @throws IOException
	 */
	@Test
	public void testSerializerTime() throws IOException{
		Order order = new Order();
		order.setCustId("123");
		Customer customer = new Customer();
		customer.setId(123);
		customer.setName("sagdfsgfdsgdsfgf");
		customer.setNumber("343e42343523423");
		order.setCustomer(customer);
	//	KryoFactory.getFactory().registerClass(Order.class);
	//	KryoFactory.getFactory().registerClass(Customer.class);
		KryoSerializer<Order> ser = new KryoSerializer<Order>();
		ser.serialize(order);
		long kryoTimeBegin = System.currentTimeMillis();
		ser.serialize(order);
		System.out.println("kryo serializer time： "  + (System.currentTimeMillis() - kryoTimeBegin));//0
		long jdkTimeBegin = System.currentTimeMillis();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(out);
	    oo.writeObject(order);
	    out.toByteArray();
	    System.out.println("JDK serializer time： "  + (System.currentTimeMillis() - jdkTimeBegin));//13
	}
	
	

}
