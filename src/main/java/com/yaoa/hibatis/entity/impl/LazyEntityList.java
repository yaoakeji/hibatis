/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.metadata.CollectionProperty;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年11月16日
 */
public class LazyEntityList<E> implements List<E>{
	
	private HibatisEntity source;
	
	private CollectionProperty collection;
	
	private List<E> list;
	
	public LazyEntityList(HibatisEntity source , CollectionProperty collection){
		this.source = source;
		this.collection = collection;
	}
	
	@SuppressWarnings("unchecked")
	private List<E> getList(){
		if(list == null){
			list = (List<E>)EntityEnhancer.loadCollection(collection, source);
		}
		return list;
	}

	public int size() {
		return getList().size();
	}

	public boolean isEmpty() {
		return getList().isEmpty();
	}

	public boolean contains(Object o) {
		return getList().contains(o);
	}

	public Iterator<E> iterator() {
		return getList().iterator();
	}

	public Object[] toArray() {
		return getList().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return getList().toArray(a);
	}

	public boolean add(E e) {
		return getList().add(e);
	}

	public boolean remove(Object o) {
		return getList().remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return getList().containsAll(c);
	}

	public boolean addAll(Collection<? extends E> c) {
		return getList().addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return getList().addAll(index , c);
	}

	public boolean removeAll(Collection<?> c) {
		return getList().removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return getList().retainAll(c);
	}

	public void clear() {
		getList().clear();
	}

	public E get(int index) {
		return getList().get(index);
	}

	public E set(int index, E element) {
		return getList().set(index, element);
	}

	public void add(int index, E element) {
		getList().add(index, element);
	}

	public E remove(int index) {
		return getList().remove(index);
	}

	public int indexOf(Object o) {
		return getList().indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return getList().lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return getList().listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return getList().listIterator(index);
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return getList().subList(fromIndex, toIndex);
	}
}
