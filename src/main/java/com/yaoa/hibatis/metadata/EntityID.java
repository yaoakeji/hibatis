/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.yaoa.hibatis.exception.HibatisException;

public class EntityID implements Map<String, Object>{
	
	private Class<?> entityType;
	
	private List<IdProperty> idProperties;
	
	private Map<String , Object> map;
	
	public EntityID(EntityMetadata metadata){
		this.entityType = metadata.getEntityType();
		this.idProperties = metadata.getIdProperties();
		map =  new HashMap<String, Object>(idProperties.size());
	}
	
	public void putFirst(Object value){
		String key = idProperties.get(0).getName();
		this.put(key, value);
	}
	
	public void putArray(Object array){
		for (int i = 0; i < Array.getLength(array); i++) {
			Object value = Array.get(array, i);
			String key = idProperties.get(i).getName();
			this.put(key, value);
		}
	}
	
	public void putObject(Object obj){
		Class<?> objClass = obj.getClass();
		if (entityType.isAssignableFrom(objClass)) {
			for (int i = 0; i < idProperties.size(); i++) {
				IdProperty property = idProperties.get(i);
				String key = property.getName();
				Object value = property.getFieldValue(obj);
				this.put(key, value);
			}
		} else {
			for (int i = 0; i < idProperties.size(); i++) {
				IdProperty property = idProperties.get(i);
				PropertyDescriptor propDesc = property.getDescriptor();
				String methodName = propDesc.getReadMethod().getName();
				Method readMethod = BeanUtils.findMethod(objClass, methodName);
				if (readMethod == null) {
					throw new HibatisException("类[" + objClass + "]方法[" + methodName + "]不存在");
				}
				try {
					String key = property.getName();
					Object value  = readMethod.invoke(obj);
					this.put(key, value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

}
